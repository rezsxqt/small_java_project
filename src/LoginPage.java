import javax.swing.*;
import java.awt.*;
import java.sql.*;
import icons.UITheme;
public class LoginPage extends JFrame {

    JTextField txtUser;
    JPasswordField txtPass;

    public LoginPage() {
        setTitle("Login");
        setSize(700, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left branding panel
        JPanel left = new JPanel();
        left.setBackground(UITheme.primary);
        left.setPreferredSize(new Dimension(280, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel app = new JLabel("MyApp");
        app.setForeground(Color.WHITE);
        app.setFont(new Font("Segoe UI", Font.BOLD, 28));
        app.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Secure Login System");
        tagline.setForeground(Color.WHITE);
        tagline.setFont(UITheme.normalFont);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(app);
        left.add(Box.createVerticalStrut(10));
        left.add(tagline);
        left.add(Box.createVerticalGlue());

        // Right form panel
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.bgColor);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(320, 280));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUser = input();
        txtPass = password();

        JButton login = UITheme.styledButton("Login");
        JButton register = link("Create new account");

        login.addActionListener(e -> login());
        register.addActionListener(e -> {
            dispose();
            new RegisterPage();
        });

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(label("Email"));
        card.add(txtUser);
        card.add(Box.createVerticalStrut(10));
        card.add(label("Password"));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(20));
        card.add(login);
        card.add(Box.createVerticalStrut(10));
        card.add(register);

        right.add(card);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        setVisible(true);
    }

    void login() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE email=? AND password=?"
            );
            ps.setString(1, txtUser.getText());
            ps.setString(2, String.valueOf(txtPass.getPassword()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Print all column names to help debug
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.println("Available columns:");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.println(metaData.getColumnName(i));
                }

                // Try to get the first text column for the name
                String userName = rs.getString(1); // Get first column
                dispose();
                new DashboardPage(userName);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JTextField input() {
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return t;
    }

    JPasswordField password() {
        JPasswordField p = new JPasswordField();
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return p;
    }

    JLabel label(String s) {
        return new JLabel(s);
    }

    JButton link(String s) {
        JButton b = new JButton(s);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(UITheme.primary);
        return b;
    }
}