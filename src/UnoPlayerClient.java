import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class UnoPlayerClient extends JPanel {
    private static JFrame window;
    private static UnoNetListener listener;
    private static int player, numPlayers;
    private static Image back;
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main(String[] args) {
//        try {
//        Socket soc = new Socket("192.168.201.1", 4200);
//        DataInputStream din = new DataInputStream(soc.getInputStream());
//        DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
//        } catch (IOException ignored) {}

        window = new JFrame("Uno");
        listener = new UnoNetListener();
        window.setSize(1920,1080);

        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        window.setContentPane(selection());
        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    private static JPanel selection() {
        numPlayers = 4;
        JPanel selection = new JPanel();
        selection.setLayout(new GridLayout(numPlayers + 1, 1)); //TODO Only have enough slots for required amt of players
        JTextArea topTxt = new JTextArea("Select which player you would like to be");
        topTxt.setEditable(false);
        JPanel topTxtBox = new JPanel();
        topTxtBox.add(topTxt);
        topTxtBox.setBackground(none);
        selection.add(topTxtBox);
        for (int i = 0; i < numPlayers; i++) {
            selection.add(new JButton("Player " + (i + 1)));
        }

        return selection;
    }

    public static void setPlayer(int newPlayer) {
        player = newPlayer;
    }
}
