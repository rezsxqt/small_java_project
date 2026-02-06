import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import javax.imageio.ImageIO;
import icons.UITheme;

public class DashboardPage extends JFrame {

    private String currentEmail;
    private JLabel photoLabel;
    private String photoPath = null;

    public DashboardPage(String userEmail) {
        this.currentEmail = userEmail;

        setTitle("Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(45, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("MyApp");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton editProfile = UITheme.styledButton("Edit Profile");
        editProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        editProfile.addActionListener(e -> showEditProfile());

        JButton logout = UITheme.styledButton("Logout");
        logout.setBackground(UITheme.danger);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(editProfile);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(20));

        // Main content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(UITheme.bgColor);

        JLabel welcome = new JLabel("Welcome, " + getUserName() + " 👋");
        welcome.setFont(UITheme.titleFont);

        content.add(welcome);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    String getUserName() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT firstname FROM users WHERE email=?"
            );
            ps.setString(1, currentEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("firstname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User";
    }

    void showEditProfile() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Photo section
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        photoPanel.setBackground(Color.WHITE);
        photoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadUserPhoto();

        JButton uploadPhoto = new JButton("Upload Photo");
        uploadPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadPhoto.addActionListener(e -> {
            uploadPhoto();
            updatePhotoInDB();
        });

        photoPanel.add(photoLabel);
        photoPanel.add(Box.createVerticalStrut(10));
        photoPanel.add(uploadPhoto);

        // Form fields with individual edit buttons
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Load current user data
        String[] userData = new String[5];
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT firstname, lastname, username, email, password FROM users WHERE email=?"
            );
            ps.setString(1, currentEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userData[0] = rs.getString("firstname");
                userData[1] = rs.getString("lastname");
                userData[2] = rs.getString("username");
                userData[3] = rs.getString("email");
                userData[4] = rs.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        formPanel.add(createEditableField("First Name", userData[0], "firstname"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createEditableField("Last Name", userData[1], "lastname"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createEditableField("Username", userData[2], "username"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createEditableField("Email", userData[3], "email"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createPasswordField("Password", userData[4]));

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.LIGHT_GRAY);
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeBtn);

        mainPanel.add(photoPanel);
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    JPanel createEditableField(String label, String currentValue, String fieldName) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblField = new JLabel(label + ":");
        lblField.setPreferredSize(new Dimension(120, 30));

        JTextField txtField = new JTextField(currentValue);
        txtField.setEditable(false);
        txtField.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setPreferredSize(new Dimension(70, 30));

        btnEdit.addActionListener(e -> {
            if (btnEdit.getText().equals("Edit")) {
                txtField.setEditable(true);
                txtField.setBackground(Color.WHITE);
                txtField.requestFocus();
                btnEdit.setText("Save");
            } else {
                String newValue = txtField.getText().trim();
                if (newValue.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, label + " cannot be empty");
                    txtField.setText(currentValue);
                    return;
                }

                if (updateField(fieldName, newValue)) {
                    txtField.setEditable(false);
                    txtField.setBackground(Color.WHITE);
                    btnEdit.setText("Edit");

                    if (fieldName.equals("email")) {
                        currentEmail = newValue;
                    }

                    JOptionPane.showMessageDialog(panel, label + " updated successfully!");

                    // Refresh dashboard
                    SwingUtilities.getWindowAncestor(panel).dispose();
                    dispose();
                    new DashboardPage(currentEmail);
                } else {
                    txtField.setText(currentValue);
                    btnEdit.setText("Edit");
                }
            }
        });

        panel.add(lblField, BorderLayout.WEST);
        panel.add(txtField, BorderLayout.CENTER);
        panel.add(btnEdit, BorderLayout.EAST);

        return panel;
    }

    JPanel createPasswordField(String label, String currentPassword) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblField = new JLabel(label + ":");
        lblField.setPreferredSize(new Dimension(120, 30));

        JPasswordField txtField = new JPasswordField("••••••••");
        txtField.setEditable(false);
        txtField.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("Change");
        btnEdit.setPreferredSize(new Dimension(70, 30));

        btnEdit.addActionListener(e -> {
            JPanel passPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            JPasswordField newPass = new JPasswordField();
            JPasswordField confirmPass = new JPasswordField();

            passPanel.add(new JLabel("New Password:"));
            passPanel.add(newPass);
            passPanel.add(new JLabel("Confirm Password:"));
            passPanel.add(confirmPass);

            int result = JOptionPane.showConfirmDialog(panel, passPanel,
                    "Change Password", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newPassword = String.valueOf(newPass.getPassword());
                String confirmPassword = String.valueOf(confirmPass.getPassword());

                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Password cannot be empty");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(panel, "Passwords do not match");
                    return;
                }

                if (updateField("password", newPassword)) {
                    JOptionPane.showMessageDialog(panel, "Password updated successfully!");
                }
            }
        });

        panel.add(lblField, BorderLayout.WEST);
        panel.add(txtField, BorderLayout.CENTER);
        panel.add(btnEdit, BorderLayout.EAST);

        return panel;
    }

    boolean updateField(String fieldName, String newValue) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET " + fieldName + "=? WHERE email=?"
            );
            ps.setString(1, newValue);
            ps.setString(2, currentEmail);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "This " + fieldName + " already exists");
            } else {
                JOptionPane.showMessageDialog(this, "Error updating " + fieldName);
            }
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating " + fieldName);
            e.printStackTrace();
            return false;
        }
    }

    void loadUserPhoto() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT photo FROM users WHERE email=?"
            );
            ps.setString(1, currentEmail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String path = rs.getString("photo");
                if (path != null && !path.isEmpty()) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        BufferedImage img = ImageIO.read(imgFile);
                        Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(scaledImg));
                        photoPath = path;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        photoLabel.setText("No Photo");
        photoLabel.setIcon(null);
    }

    void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"
        );
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
                photoLabel.setText("");
                photoPath = selectedFile.getAbsolutePath();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image");
                e.printStackTrace();
            }
        }
    }

    void updatePhotoInDB() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET photo=? WHERE email=?"
            );
            ps.setString(1, photoPath);
            ps.setString(2, currentEmail);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Photo updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating photo");
            e.printStackTrace();
        }
    }
}