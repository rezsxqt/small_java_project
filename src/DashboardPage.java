import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

public class DashboardPage extends JFrame {
    private int userId;
    private String userFirstname;
    private String userLastname;
    private String userPhotoPath;  // Store photo path

    public DashboardPage(int userId) {
        this.userId = userId;  // Store the logged-in user's ID

        // Load user's name first
        loadUserName();

        // === WINDOW SETUP ===
        setTitle("Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen
        setResizable(false);

        // === CREATE MENU BAR ===
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(52, 73, 94));
        menuBar.setPreferredSize(new Dimension(900, 40));

        // Dashboard Menu
        JMenu dashboardMenu = createStyledMenu("Dashboard");
        JMenuItem homeItem = createStyledMenuItem("Home");
        homeItem.addActionListener(e -> showWelcomeScreen());
        dashboardMenu.add(homeItem);

        // Account Menu
        JMenu accountMenu = createStyledMenu("Account");
        JMenuItem accountDetailsItem = createStyledMenuItem("Account Details");
        accountDetailsItem.addActionListener(e -> openAccountDetails());
        JMenuItem editDetailsItem = createStyledMenuItem("Edit Details");
        editDetailsItem.addActionListener(e -> openEditDialog());
        JMenuItem deleteAccountItem = createStyledMenuItem("Delete Account");
        deleteAccountItem.addActionListener(e -> deleteAccount());
        accountMenu.add(accountDetailsItem);
        accountMenu.add(editDetailsItem);
        accountMenu.addSeparator();
        accountMenu.add(deleteAccountItem);

        // Logout Menu
        JMenu logoutMenu = createStyledMenu("Logout");
        logoutMenu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });

        menuBar.add(dashboardMenu);
        menuBar.add(accountMenu);
        menuBar.add(Box.createHorizontalGlue()); // Push logout to right
        menuBar.add(logoutMenu);

        setJMenuBar(menuBar);

        // === MAIN CONTENT - WELCOME SCREEN ===
        showWelcomeScreen();
    }

    /**
     * Create styled menu for menu bar
     */
    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        return menu;
    }

    /**
     * Create styled menu item
     */
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 13));
        return item;
    }

    /**
     * Show welcome screen (main dashboard view)
     */
    private void showWelcomeScreen() {
        // Clear current content
        getContentPane().removeAll();

        // Main panel with GridLayout for two panels
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // === LEFT PANEL - PROFILE PHOTO SECTION ===
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(52, 152, 219));  // Blue color
        leftPanel.setLayout(new GridBagLayout());  // Centers content

        // Create a container for photo and label
        JPanel photoContainer = new JPanel();
        photoContainer.setLayout(new BoxLayout(photoContainer, BoxLayout.Y_AXIS));
        photoContainer.setBackground(new Color(52, 152, 219));

        // Profile Photo Label
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setMaximumSize(new Dimension(150, 150));
        photoLabel.setMinimumSize(new Dimension(150, 150));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.WHITE);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Load and display photo
        if (userPhotoPath != null && !userPhotoPath.isEmpty()) {
            File photoFile = new File(userPhotoPath);
            if (photoFile.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(userPhotoPath);
                    Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(image));
                } catch (Exception e) {
                    // If image loading fails, show default
                    photoLabel.setText("👤");
                    photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
                }
            } else {
                // Photo file doesn't exist
                photoLabel.setText("👤");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }
        } else {
            // No photo path in database
            photoLabel.setText("👤");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        photoContainer.add(photoLabel);
        photoContainer.add(Box.createVerticalStrut(20));

        // "User Profile" label below photo
        JLabel profileLabel = new JLabel("User Profile");
        profileLabel.setFont(new Font("Arial", Font.BOLD, 20));
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoContainer.add(profileLabel);

        leftPanel.add(photoContainer);

        // === RIGHT PANEL - WELCOME MESSAGE ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        JLabel welcomeText = new JLabel("Welcome Back");
        welcomeText.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeText.setForeground(new Color(52, 73, 94));
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameText = new JLabel(userFirstname + " " + userLastname);
        nameText.setFont(new Font("Arial", Font.BOLD, 42));
        nameText.setForeground(new Color(52, 152, 219));
        nameText.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(welcomeText);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(nameText);

        rightPanel.add(centerPanel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
        revalidate();
        repaint();
    }

    /**
     * Load user's first name, last name, and photo from database
     */
    private void loadUserName() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT firstname, lastname, photo FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userFirstname = rs.getString("firstname");
                userLastname = rs.getString("lastname");
                userPhotoPath = rs.getString("photo");  // Load photo path
            }
        } catch (SQLException ex) {
            userFirstname = "User";
            userLastname = "";
            userPhotoPath = null;
        }
    }

    /**
     * Open Account Details page
     */
    private void openAccountDetails() {
        new AccountDetailsPage(userId).setVisible(true);
        dispose();
    }

    /**
     * Open edit details dialog
     */
    private void openEditDialog() {
        // Load current user data first
        String currentFirstname = "", currentLastname = "", currentPhone = "";
        String currentEmail = "", currentUsername = "", currentPhotoPath = "";

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentFirstname = rs.getString("firstname");
                currentLastname = rs.getString("lastname");
                currentPhone = rs.getString("phone");
                currentEmail = rs.getString("email");
                currentUsername = rs.getString("username");
                currentPhotoPath = rs.getString("photo");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        JTextField fnField = new JTextField(currentFirstname);
        fnField.setBounds(150, yPos, 300, 30);
        editDialog.add(fnField);

        yPos += 50;

        JLabel lnLabel = new JLabel("Last Name:");
        lnLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(lnLabel);

        JTextField lnField = new JTextField(currentLastname);
        lnField.setBounds(150, yPos, 300, 30);
        editDialog.add(lnField);

        yPos += 50;

        JLabel phLabel = new JLabel("Phone:");
        phLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(phLabel);

        JTextField phField = new JTextField(currentPhone);
        phField.setBounds(150, yPos, 300, 30);
        editDialog.add(phField);

        yPos += 50;

        JLabel emLabel = new JLabel("Email:");
        emLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(emLabel);

        JTextField emField = new JTextField(currentEmail);
        emField.setBounds(150, yPos, 300, 30);
        editDialog.add(emField);

        yPos += 50;

        JLabel unLabel = new JLabel("Username:");
        unLabel.setBounds(50, yPos, 100, 25);
        editDialog.add(unLabel);

        JTextField unField = new JTextField(currentUsername);
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
        String finalCurrentPhotoPath = currentPhotoPath;
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
                        query = "UPDATE users SET firstname=?, lastname=?, phone=?, email=?, username=?, photo=? WHERE id=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, firstname);
                        pstmt.setString(2, lastname);
                        pstmt.setString(3, phone);
                        pstmt.setString(4, email);
                        pstmt.setString(5, username);
                        pstmt.setString(6, selectedPhotoPath[0] != null ? selectedPhotoPath[0] : finalCurrentPhotoPath);
                        pstmt.setInt(7, userId);
                    } else {
                        query = "UPDATE users SET firstname=?, lastname=?, phone=?, email=?, username=?, password=?, photo=? WHERE id=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, firstname);
                        pstmt.setString(2, lastname);
                        pstmt.setString(3, phone);
                        pstmt.setString(4, email);
                        pstmt.setString(5, username);
                        pstmt.setString(6, password);
                        pstmt.setString(7, selectedPhotoPath[0] != null ? selectedPhotoPath[0] : finalCurrentPhotoPath);
                        pstmt.setInt(8, userId);
                    }

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(editDialog, "Details updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUserName();
                        showWelcomeScreen();
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

    /**
     * Delete user account from database
     */
    private void deleteAccount() {
        // STEP 1: Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // STEP 2: If user confirms, delete from database
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                // SQL query to delete user
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, userId);  // Set the user ID to delete

                int result = pstmt.executeUpdate();

                // STEP 3: If deleted successfully, return to login
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Account deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    new LoginPage().setVisible(true);
                    dispose();  // Close dashboard
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting account: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logout and return to login page
     */
    private void logout() {
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        // If confirmed, go to login page
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();  // Close dashboard
        }
    }
}