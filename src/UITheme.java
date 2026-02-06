package icons;
import java.awt.*;
import javax.swing.*;

public class UITheme {

    public static Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
    public static Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public static Color bgColor = new Color(245, 247, 250);
    public static Color cardColor = Color.WHITE;
    public static Color primary = new Color(52, 152, 219);
    public static Color danger = new Color(231, 76, 60);

    public static JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(primary);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(normalFont);
        return btn;
    }
}

