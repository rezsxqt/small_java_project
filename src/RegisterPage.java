import javax.swing.*;
import java.awt.*;
import java.sql.*;
import icons.UITheme;
public class RegisterPage extends JFrame {

    JTextField fName, lName, phone, email, user;
    JPasswordField pass, confirm;

    public RegisterPage() {
        setTitle("Register");
        setSize(780, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setBackground(UITheme.primary);
        left.setPreferredSize(new Dimension(300, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Join MyApp");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(title);
        left.add(Box.createVerticalGlue());

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.bgColor);

        JPanel card = new JPanel(new GridLayout(8, 2, 10, 10));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(380, 400));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        fName = new JTextField();
        lName = new JTextField();
        phone = new JTextField();
        email = new JTextField();
        user = new JTextField();
        pass = new JPasswordField();
        confirm = new JPasswordField();

        JButton register = UITheme.styledButton("Create Account");
        JButton back = link("Back to Login");

        register.addActionListener(e -> register());
        back.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        card.add(new JLabel("First Name")); card.add(fName);
        card.add(new JLabel("Last Name")); card.add(lName);
        card.add(new JLabel("Phone")); card.add(phone);
        card.add(new JLabel("Email")); card.add(email);
        card.add(new JLabel("Username")); card.add(user);
        card.add(new JLabel("Password")); card.add(pass);
        card.add(new JLabel("Confirm Password")); card.add(confirm);
        card.add(register); card.add(back);

        right.add(card);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        setVisible(true);
    }

    void register() {
        if (fName.getText().trim().isEmpty() || lName.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty() || email.getText().trim().isEmpty() ||
                user.getText().trim().isEmpty() || pass.getPassword().length == 0 ||
                confirm.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }

        if (!String.valueOf(pass.getPassword())
                .equals(String.valueOf(confirm.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(firstname,lastname,phone,email,username,password) VALUES(?,?,?,?,?,?)"
            );
            ps.setString(1, fName.getText().trim());
            ps.setString(2, lName.getText().trim());
            ps.setString(3, phone.getText().trim());
            ps.setString(4, email.getText().trim());
            ps.setString(5, user.getText().trim());
            ps.setString(6, String.valueOf(pass.getPassword()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account created!");
            dispose();
            new LoginPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "User or email already exists");
        }
    }

    JButton link(String s) {
        JButton b = new JButton(s);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(UITheme.primary);
        return b;
    }
}