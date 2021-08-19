import javax.swing.*;

public class UnoPlayerClient extends JPanel {
    private static JFrame window;
    private static UnoListener listener;

    public static void main(String[] args) {
        window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        listener = new UnoListener(panel);
        window.setSize(1920,1080);
    }
}
