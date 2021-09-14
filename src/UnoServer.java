import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import com.fasterxml.jackson.databind.*;

public class UnoServer extends JPanel {
    private static JFrame window;
    private static ServerListener listener;
    private ServerSocket servsoc;
    private Socket soc;
    private InputStreamReader din;
    private OutputStreamWriter dout;
    private UnoGraphicsGame game;
    private UnoNetData data;
    private static Image back;
    private static Container c;
    private static CardLayout screen = new CardLayout();
    private int targetWidth = 1920, targetHeight = 1080;
    private PanelDims dims = new PanelDims(targetWidth, targetHeight);
    private static final String ARIAL = "Arial";
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main (String[] args) {
        window = new JFrame("Uno Server");
        UnoServer server = new UnoServer();
        listener = new ServerListener(server);

        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        c = window.getContentPane();
        c.setLayout(screen);
        c.add(serverPanel());

        window.setSize(1920,1080);
        window.setContentPane(c);
        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    private static JPanel serverPanel() {
        //Creates initial home screen for the server
        JPanel home = new JPanel();
        home.setLayout(new GridLayout(2, 1));

        JPanel top = new JPanel();
        top.setLayout(new GridLayout(2, 1));
        top.add(new JLabel(new ImageIcon(back.getScaledInstance(100, 143, Image.SCALE_SMOOTH))));
        JTextArea topTxt = new JTextArea("Uno Server");
        topTxt.setEditable(false);
        topTxt.setFont(new Font(ARIAL, Font.PLAIN, 75));
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
        start.addActionListener(listener);
        start.setFont(new Font(ARIAL, Font.PLAIN, 50));
        bottom.add(start);
        bottom.setBackground(none);
        home.add(bottom);

        return home;
    }

    public void start() {
        //Starts up the server and the game, running it until the end
        c.add(waitConn());
        screen.next(c);

        boolean connected = false;
        while (!connected) {
            try {
                System.out.println("Attempting to open socket");
                servsoc = new ServerSocket(4200);
                System.out.println("Socket state: " + servsoc);
                soc = servsoc.accept();
                System.out.println("Socket accepted");
                din = new InputStreamReader(soc.getInputStream());
                System.out.println("Got input stream");
                dout = new OutputStreamWriter(soc.getOutputStream());
                System.out.println("Socket opened");
                connected = true;
            } catch (IOException fatalError) {
                System.out.println("A fatal error has occurred.\nConnection to the server could not be established.");
                System.exit(0);
            }
        }

        game = new UnoGraphicsGame(listener.getPlayerCount());
        c.add(gameMenu());
        screen.next(c);

        while (game.determineWinner() == -1) {
            //Output game data
            data = new UnoNetData(game.getPlacePile(), null, game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft(), -1, null);
            try {
                setPanelDims(window.getWidth(), window.getHeight());
                writeJSON(data);
                dout.flush();

                data = readJSON();
            } catch (IOException ignored) {}
            //Take in game data from players
            try {
                readJSON();
            } catch (IOException ignored) {}
        }

        try {
            System.out.println("Closing socket");
            servsoc.close();
            soc.close();
            dout.close();
            din.close();
        } catch (IOException ignored) {}
    }

    private void writeJSON(UnoNetData toJSON) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(dout, toJSON);
    }

    private UnoNetData readJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.readValue(din, UnoNetData.class);
        return data;
    }

    private JPanel waitConn() {
        JPanel waitConn = new JPanel(new BorderLayout());
        JTextArea waiting = new JTextArea();
        waiting.setEditable(false);
        waiting.setFont(new Font(ARIAL, Font.PLAIN, 75));
        waitConn.add(waiting, BorderLayout.CENTER);

        return waitConn;
    }

    private JPanel gameMenu() {
        //Creates panel for when the game is running and displays info about the game
        JPanel gameMenu = new JPanel(new GridLayout(listener.getPlayerCount() + 3, 1));
        JLabel infoTxt = new JLabel("Game Info");
        JButton menu = new JButton("Menu");
        JPanel veryTop = new JPanel(new GridLayout(1, 2));
        veryTop.add(infoTxt);
        veryTop.add(menu);
        gameMenu.add(veryTop);
        targetHeight = dims.getHeight() / listener.getPlayerCount() + 4;
        targetWidth = targetHeight / 143 * 100;
        Image img = game.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        JLabel placePile = new JLabel(new ImageIcon(img));
        JLabel cardsLeft = new JLabel("Cards left: " + game.getCardsLeft());
        JPanel cardInfo = new JPanel(new GridLayout(1, 2));
        cardInfo.add(placePile);
        cardInfo.add(cardsLeft);
        gameMenu.add(cardInfo);
        // Player Cards

        return gameMenu;
    }


    public void goMenu() {
        c.setVisible(false);
        c.removeAll();
        c.add(serverPanel());
        c.setVisible(true);
    }

    private void setPanelDims(int width, int height) {
        targetHeight = height;
        targetWidth = width;
    }
}
