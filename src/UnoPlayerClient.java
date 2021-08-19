import javax.swing.*;
import java.io.*;
import java.net.*;

public class UnoPlayerClient extends JPanel {
    private static JFrame window;
    private static UnoNetListener listener;

    public static void main(String[] args) throws IOException {
        window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        listener = new UnoNetListener(panel);
        window.setSize(1920,1080);

        Socket soc = new Socket("192.168.201.1", 4200);
        DataInputStream din = new DataInputStream(soc.getInputStream());
        DataOutputStream dout = new DataOutputStream(soc.getOutputStream());


    }
}
