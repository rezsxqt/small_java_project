import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

public class AccountDetailsPage extends JFrame {
    private int userId;
    private JLabel photoLabel;
    private JLabel firstnameDisplay, lastnameDisplay, phoneDisplay, emailDisplay, usernameDisplay;
    private String currentPhotoPath = null;

    public AccountDetailsPage(int userId) {
        this.userId = userId;

        // === WINDOW SETUP ===
        setTitle("Account Details");
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
        homeItem.addActionListener(e -> goToDashboard());
        dashboardMenu.add(homeItem);

        // Account Menu
        JMenu accountMenu = createStyledMenu("Account");
        JMenuItem accountDetailsItem = createStyledMenuItem("Account Details");
        accountDetailsItem.addActionListener(e -> {
            // Already on this page, just refresh
            loadUserData();
        });
        JMenuItem editDetailsItem = createStyledMenuItem("Edit Details");
        editDetailsItem.addActionListener(e -> goToDashboardAndEdit());
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
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(logoutMenu);

        setJMenuBar(menuBar);

        // === MAIN CONTENT PANEL ===
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // === LEFT PANEL - PROFILE PHOTO ===
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

        // === RIGHT PANEL - USER DETAILS ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Account Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(120, 40, 250, 40);
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

        // Back to Dashboard Button
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBounds(120, 450, 200, 35);
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> goToDashboard());
        rightPanel.add(backButton);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);

        // Load user data
        loadUserData();
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
     * Load user data from database
     */
    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            // Query to get all user details
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Display all user information
                firstnameDisplay.setText(rs.getString("firstname"));
                lastnameDisplay.setText(rs.getString("lastname"));
                phoneDisplay.setText(rs.getString("phone"));
                emailDisplay.setText(rs.getString("email"));
                usernameDisplay.setText(rs.getString("username"));
                currentPhotoPath = rs.getString("photo");

                // Load and display photo if exists
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
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Go back to dashboard
     */
    private void goToDashboard() {
        new DashboardPage(userId).setVisible(true);
        dispose();
    }

    /**
     * Go to dashboard and open edit dialog
     */
    private void goToDashboardAndEdit() {
        DashboardPage dashboard = new DashboardPage(userId);
        dashboard.setVisible(true);
        dispose();
        // The edit dialog will be opened from the dashboard menu
    }

    /**
     * Delete account
     */
    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, userId);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Account deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    new LoginPage().setVisible(true);
                    dispose();
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
     * Logout
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }
}