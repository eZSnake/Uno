import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class UnoPlayerServer {
    private static JFrame window;
    private static UnoServerListener listener;
    private static Image back;
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main (String[] args) {
//        try {
//            ServerSocket servsoc = new ServerSocket(4200);
//            Socket soc = servsoc.accept();
//            DataInputStream din = new DataInputStream(soc.getInputStream());
//            DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
//        } catch (IOException ignored) {}

        window = new JFrame("Uno Server");
        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        UnoPlayerServer server = new UnoPlayerServer();
        listener = new UnoServerListener(server);
        window.setSize(1920,1080);
        window.setContentPane(serverPanel());
        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    static JPanel serverPanel() {
        JPanel home = new JPanel();
        home.setLayout(new GridLayout(2, 1));
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(2, 1));
        top.add(new JLabel(new ImageIcon(back.getScaledInstance(100, 143, Image.SCALE_SMOOTH))));
        JTextArea topTxt = new JTextArea("Uno Server");
        topTxt.setEditable(false);
        topTxt.setFont(new Font("Arial", Font.PLAIN, 75));
        JPanel topTxtBox = new JPanel();
        topTxtBox.add(topTxt);
        topTxtBox.setBackground(none);
        top.add(topTxtBox);
        top.setBackground(none);
        home.add(top);
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(2, 1));
        JSlider playerCount = new JSlider(2, 4, 2);
        playerCount.addChangeListener(listener);
        playerCount.setMajorTickSpacing(1);
        playerCount.setPaintTicks(true);
        playerCount.setPaintLabels(true);
        playerCount.setBackground(none);
        bottom.add(playerCount);
        JButton start = new JButton("Start");
        start.setFont(new Font("Arial", Font.PLAIN, 50));
        bottom.add(start);
        bottom.setBackground(none);
        home.add(bottom);

        return home;
    }

    public void start() {

    }

    public void menu() {

    }
}
