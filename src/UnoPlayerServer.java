import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class UnoPlayerServer {
    private static JFrame window;
    private static UnoServerListener listener;
    private ObjectInputStream din;
    private ObjectOutputStream dout;
    private UnoGraphicsGame game;
    private static Image back;
    private static Container c;
    private static CardLayout screen = new CardLayout();
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main (String[] args) {
        window = new JFrame("Uno Server");
        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        c = window.getContentPane();
        c.setLayout(screen);
        c.add(serverPanel());

        UnoPlayerServer server = new UnoPlayerServer();
        listener = new UnoServerListener(server);

        window.setSize(1920,1080);
        window.setContentPane(c);
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
        try {
            ServerSocket servsoc = new ServerSocket(4200);
            Socket soc = servsoc.accept();
            din = new ObjectInputStream(soc.getInputStream());
            dout = new ObjectOutputStream(soc.getOutputStream());
        } catch (IOException fatalError) {
            System.out.println("A fatal error has occurred.\nConnection to the server could not be established.");
            System.exit(0);
        }

        game = new UnoGraphicsGame(listener.getPlayerCount());

        while (game.determineWinner() == -1) {
            //Output game data
            UnoNetData toSend = new UnoNetData(game.getPlacePile(), game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft());
            dout.write(toSend); //TODO Figure out how to send data
            //Take in game data from players
        }
    }

    public JPanel gameMenu() {
        JPanel gameMenu = new JPanel();

        return gameMenu;
    }
}
