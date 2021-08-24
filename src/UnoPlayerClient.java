import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UnoPlayerClient extends JPanel {
    private static JFrame window;
    private static UnoNetListener listener;
    private static int player = 0, numPlayers;
    private static Image back;
    private static Container c;
    private CardLayout screen;
    private static ArrayList<JLabel> selectedPlayer = new ArrayList<>();
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main(String[] args) {
//        try {
//        Socket soc = new Socket("192.168.201.1", 4200);
//        DataInputStream din = new DataInputStream(soc.getInputStream());
//        DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
//        } catch (IOException ignored) {}

        window = new JFrame("Uno");
        UnoPlayerClient client = new UnoPlayerClient();
        listener = new UnoNetListener(client);
        window.setSize(1920,1080);

        c = window.getContentPane();

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
        selection.setLayout(new GridLayout(numPlayers + 3, 1)); //TODO Only have enough slots for required amt of players
        JTextArea topTxt = new JTextArea("\nSelect which player you would like to be");
        topTxt.setFont(new Font("Arial", Font.PLAIN, 50));
        topTxt.setEditable(false);
        JPanel topTxtBox = new JPanel();
        topTxtBox.add(topTxt);
        topTxtBox.setBackground(none);
        selection.add(topTxtBox);
        for (int i = 0; i < numPlayers; i++) {
            JButton tempButton = new JButton("Player " + (i + 1));
            tempButton.setFont(new Font("Arial", Font.PLAIN, 35));
            tempButton.addActionListener(listener);
            selection.add(tempButton);
        }
        JLabel botTxt = new JLabel("Currently player " + player);
        botTxt.setFont(new Font("Arial", Font.PLAIN, 50));
        selectedPlayer.add(botTxt);
        JPanel botTxtBox = new JPanel();
        botTxtBox.add(selectedPlayer.get(0));
        botTxtBox.setBackground(none);
        selection.add(botTxtBox);
        JButton cont = new JButton("Continue");
        cont.setFont(new Font("Arial", Font.PLAIN, 35));
        cont.addActionListener(listener);
        selection.add(cont);

        return selection;
    }

    public static void setPlayer(int newPlayer) {
        player = newPlayer;
        selectedPlayer.get(0).setText("Currently player " + player);
    }

    public void nextScreen() {
        screen.next(c);
    }
}
