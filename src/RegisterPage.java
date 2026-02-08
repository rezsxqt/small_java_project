import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    private JTextField firstnameField, lastnameField, phoneField, emailField, usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton;
    private JLabel loginLabel;

    public RegisterPage() {
        setTitle("Register");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with GridLayout for two panels
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel - Welcome Section
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(41, 128, 185));
        leftPanel.setLayout(new GridBagLayout());

        JLabel welcomeLabel = new JLabel("<html><center>Join Us<br>Today!</center></html>");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);

        leftPanel.add(welcomeLabel);

        // Right Panel - Form Section
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(150, 30, 200, 40);
        rightPanel.add(titleLabel);

        // First Name
        JLabel firstnameLabel = new JLabel("First Name:");
        firstnameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        firstnameLabel.setBounds(50, 90, 100, 20);
        rightPanel.add(firstnameLabel);

        firstnameField = new JTextField();
        firstnameField.setFont(new Font("Arial", Font.PLAIN, 12));
        firstnameField.setBounds(50, 112, 350, 30);
        firstnameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(firstnameField);

        // Last Name
        JLabel lastnameLabel = new JLabel("Last Name:");
        lastnameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        lastnameLabel.setBounds(50, 150, 100, 20);
        rightPanel.add(lastnameLabel);

        lastnameField = new JTextField();
        lastnameField.setFont(new Font("Arial", Font.PLAIN, 12));
        lastnameField.setBounds(50, 172, 350, 30);
        lastnameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(lastnameField);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        phoneLabel.setBounds(50, 210, 100, 20);
        rightPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setFont(new Font("Arial", Font.PLAIN, 12));
        phoneField.setBounds(50, 232, 350, 30);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(phoneField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setBounds(50, 270, 100, 20);
        rightPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailField.setBounds(50, 292, 350, 30);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(emailField);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameLabel.setBounds(50, 330, 100, 20);
        rightPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setBounds(50, 352, 350, 30);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordLabel.setBounds(50, 390, 100, 20);
        rightPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBounds(50, 412, 350, 30);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(passwordField);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmPasswordLabel.setBounds(50, 450, 150, 20);
        rightPanel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmPasswordField.setBounds(50, 472, 350, 30);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        rightPanel.add(confirmPasswordField);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBounds(50, 520, 350, 35);
        registerButton.setBackground(new Color(41, 128, 185));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        rightPanel.add(registerButton);

        JLabel alreadyUserLabel = new JLabel("Already a user?");
        alreadyUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        alreadyUserLabel.setBounds(130, 565, 100, 25);
        rightPanel.add(alreadyUserLabel);

        loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginLabel.setForeground(new Color(41, 128, 185));
        loginLabel.setBounds(230, 565, 60, 25);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openLoginPage();
            }
        });
        rightPanel.add(loginLabel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private void handleRegister() {
        String firstname = firstnameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert into database
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (firstname, lastname, phone, email, username, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, firstname);
            pstmt.setString(2, lastname);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, username);
            pstmt.setString(6, password);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                openLoginPage();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openLoginPage() {
        new LoginPage().setVisible(true);
        dispose();
    }
}