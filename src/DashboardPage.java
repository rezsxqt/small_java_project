import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

public class DashboardPage extends JFrame {
    private int userId;
    private JLabel photoLabel;
    private JLabel firstnameDisplay, lastnameDisplay, phoneDisplay, emailDisplay, usernameDisplay;
    private String currentPhotoPath = null;

    public DashboardPage(int userId) {
        this.userId = userId;

        setTitle("Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with GridLayout for two panels
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel - Profile Section
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(52, 152, 219));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.add(Box.createVerticalStrut(80));

        // Photo display
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setMaximumSize(new Dimension(150, 150));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.WHITE);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(photoLabel);

        leftPanel.add(Box.createVerticalStrut(20));

        JLabel profileLabel = new JLabel("User Profile");
        profileLabel.setFont(new Font("Arial", Font.BOLD, 28));
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(profileLabel);

        // Right Panel - Details Section
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(150, 40, 200, 40);
        rightPanel.add(titleLabel);

        // Display fields
        int yPos = 120;

        JLabel fnLabel = new JLabel("First Name:");
        fnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fnLabel.setBounds(50, yPos, 120, 25);
        rightPanel.add(fnLabel);

        firstnameDisplay = new JLabel();
        firstnameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        firstnameDisplay.setBounds(170, yPos, 250, 25);
        rightPanel.add(firstnameDisplay);

        yPos += 40;

        JLabel lnLabel = new JLabel("Last Name:");
        lnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lnLabel.setBounds(50, yPos, 120, 25);
        rightPanel.add(lnLabel);

        lastnameDisplay = new JLabel();
        lastnameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        lastnameDisplay.setBounds(170, yPos, 250, 25);
        rightPanel.add(lastnameDisplay);

        yPos += 40;

        JLabel phLabel = new JLabel("Phone:");
        phLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phLabel.setBounds(50, yPos, 120, 25);
        rightPanel.add(phLabel);

        phoneDisplay = new JLabel();
        phoneDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneDisplay.setBounds(170, yPos, 250, 25);
        rightPanel.add(phoneDisplay);

        yPos += 40;

        JLabel emLabel = new JLabel("Email:");
        emLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emLabel.setBounds(50, yPos, 120, 25);
        rightPanel.add(emLabel);

        emailDisplay = new JLabel();
        emailDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        emailDisplay.setBounds(170, yPos, 250, 25);
        rightPanel.add(emailDisplay);

        yPos += 40;

        JLabel unLabel = new JLabel("Username:");
        unLabel.setFont(new Font("Arial", Font.BOLD, 14));
        unLabel.setBounds(50, yPos, 120, 25);
        rightPanel.add(unLabel);

        usernameDisplay = new JLabel();
        usernameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameDisplay.setBounds(170, yPos, 250, 25);
        rightPanel.add(usernameDisplay);

        // Buttons
        JButton editButton = new JButton("Edit Details");
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setBounds(50, 400, 160, 35);
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEditDialog();
            }
        });
        rightPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBounds(240, 400, 160, 35);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAccount();
            }
        });
        rightPanel.add(deleteButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBounds(145, 455, 160, 35);
        logoutButton.setBackground(new Color(149, 165, 166));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        rightPanel.add(logoutButton);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                firstnameDisplay.setText(rs.getString("firstname"));
                lastnameDisplay.setText(rs.getString("lastname"));
                phoneDisplay.setText(rs.getString("phone"));
                emailDisplay.setText(rs.getString("email"));
                usernameDisplay.setText(rs.getString("username"));
                currentPhotoPath = rs.getString("photo");

                // Load photo if exists
                if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                    File photoFile = new File(currentPhotoPath);
                    if (photoFile.exists()) {
                        ImageIcon icon = new ImageIcon(currentPhotoPath);
                        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(image));
                    } else {
                        photoLabel.setText("No Photo");
                    }
                } else {
                    photoLabel.setText("No Photo");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditDialog() {
        JDialog editDialog = new JDialog(this, "Edit Details", true);
        editDialog.setSize(500, 600);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(null);

        JLabel titleLabel = new JLabel("Edit Your Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(150, 20, 200, 30);
        editDialog.add(titleLabel);

        int yPos = 70;

        JLabel fnLabel = new JLabel("First Name:");
        fnLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(fnLabel);

        JTextField fnField = new JTextField(firstnameDisplay.getText());
        fnField.setBounds(150, yPos, 300, 30);
        editDialog.add(fnField);

        yPos += 50;

        JLabel lnLabel = new JLabel("Last Name:");
        lnLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(lnLabel);

        JTextField lnField = new JTextField(lastnameDisplay.getText());
        lnField.setBounds(150, yPos, 300, 30);
        editDialog.add(lnField);

        yPos += 50;

        JLabel phLabel = new JLabel("Phone:");
        phLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(phLabel);

        JTextField phField = new JTextField(phoneDisplay.getText());
        phField.setBounds(150, yPos, 300, 30);
        editDialog.add(phField);

        yPos += 50;

        JLabel emLabel = new JLabel("Email:");
        emLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(emLabel);

        JTextField emField = new JTextField(emailDisplay.getText());
        emField.setBounds(150, yPos, 300, 30);
        editDialog.add(emField);

        yPos += 50;

        JLabel unLabel = new JLabel("Username:");
        unLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(unLabel);

        JTextField unField = new JTextField(usernameDisplay.getText());
        unField.setBounds(150, yPos, 300, 30);
        editDialog.add(unField);

        yPos += 50;

        JLabel pwLabel = new JLabel("Password:");
        pwLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(pwLabel);

        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(150, yPos, 300, 30);
        editDialog.add(pwField);

        yPos += 50;

        JLabel photoPathLabel = new JLabel("Photo: Not selected");
        photoPathLabel.setBounds(150, yPos, 300, 25);
        editDialog.add(photoPathLabel);

        final String[] selectedPhotoPath = {null};

        JButton selectPhotoButton = new JButton("Select Photo");
        selectPhotoButton.setBounds(50, yPos, 120, 30);
        selectPhotoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Image files", "jpg", "jpeg", "png", "gif"));
                int result = fileChooser.showOpenDialog(editDialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedPhotoPath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                    photoPathLabel.setText("Photo: " + fileChooser.getSelectedFile().getName());
                }
            }
        });
        editDialog.add(selectPhotoButton);

        yPos += 60;

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(100, yPos, 140, 35);
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String firstname = fnField.getText().trim();
                String lastname = lnField.getText().trim();
                String phone = phField.getText().trim();
                String email = emField.getText().trim();
                String username = unField.getText().trim();
                String password = new String(pwField.getPassword());

                if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || username.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Please fill in all required fields!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String query;
                    PreparedStatement pstmt;

                    if (password.isEmpty()) {
                        // Update without password
                        query = "UPDATE users SET firstname=?, lastname=?, phone=?, email=?, username=?, photo=? WHERE id=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, firstname);
                        pstmt.setString(2, lastname);
                        pstmt.setString(3, phone);
                        pstmt.setString(4, email);
                        pstmt.setString(5, username);
                        pstmt.setString(6, selectedPhotoPath[0] != null ? selectedPhotoPath[0] : currentPhotoPath);
                        pstmt.setInt(7, userId);
                    } else {
                        // Update with password
                        query = "UPDATE users SET firstname=?, lastname=?, phone=?, email=?, username=?, password=?, photo=? WHERE id=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, firstname);
                        pstmt.setString(2, lastname);
                        pstmt.setString(3, phone);
                        pstmt.setString(4, email);
                        pstmt.setString(5, username);
                        pstmt.setString(6, password);
                        pstmt.setString(7, selectedPhotoPath[0] != null ? selectedPhotoPath[0] : currentPhotoPath);
                        pstmt.setInt(8, userId);
                    }

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(editDialog, "Details updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUserData();
                        editDialog.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Error updating details: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        editDialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(260, yPos, 140, 35);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });
        editDialog.add(cancelButton);

        editDialog.setVisible(true);
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This action cannot be undone!",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, userId);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Account deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    new LoginPage().setVisible(true);
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting account: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }
}