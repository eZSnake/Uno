import javax.swing.*;
import java.awt.*;

public class UnoGraphicsRunner {
    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        UnoListener listener = new UnoListener(panel);
        window.setSize(1920,1080);

        window.setLocation(300,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
