import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;

import com.fasterxml.jackson.databind.*;

public class UnoServer extends JPanel {
    private Logger logger = Logger.getLogger("UnoServer");
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
    private ArrayList<JPanel> botCards = new ArrayList<>(), pCards = new ArrayList<>();
    private ArrayList<JLabel> playerCardsLeft = new ArrayList<>(), drawCardsLeft = new ArrayList<>(), placePileCard = new ArrayList<>();
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
        // Creates initial home screen for the server
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
        // Starts up the server and the game, running it until the end
        System.out.println(c.getComponentCount());
        c.add(waitConn());
        System.out.println(c.getComponentCount());
        screen.next(c);

        boolean connected = false;
        while (!connected) {
            try {
                logger.log(Level.INFO, "Attempting to open socket");
                servsoc = new ServerSocket(4200);
                logger.log(Level.INFO, "Socket state: " + servsoc);
                soc = servsoc.accept();
                din = new InputStreamReader(soc.getInputStream());
                dout = new OutputStreamWriter(soc.getOutputStream());
                logger.log(Level.INFO, "Socket opened");
                connected = true;
            } catch (IOException fatalError) {
                logger.log(Level.SEVERE, "A fatal error has occurred.\nConnection to the server could not be established.");
                System.exit(0);
            }
        }

        game = new UnoGraphicsGame(listener.getPlayerCount());
        data = new UnoNetData(game.getPlacePile(), null, game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft(), -1, null);
        logger.log(Level.INFO, "Game created");

        c.add(gameMenu());
        screen.next(c);

        synchronized (data) {
            logger.log(Level.INFO, "In synchronized 1");

            data.notifyAll();
        }
        synchronized (data) {
            logger.log(Level.INFO, "In synchronized 2");

            try {
                data.wait(500);
            } catch (InterruptedException interruptedException) {
                logger.log(Level.SEVERE, "Error: " + interruptedException);
                Thread.currentThread().interrupt();
            }
        }

        logger.log(Level.INFO, "Starting game and synchronized");
        while (game.determineWinner() == -1) {
            setPanelDims(window.getWidth(), window.getHeight());
            // Output game data
            data = new UnoNetData(game.getPlacePile(), null, game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft(), -1, null);
            try {
                writeJSON(data);
                data.notifyAll();
            } catch (IOException ignored) {}

            // Take in game data from players
            try {
                readJSON();
            } catch (IOException ignored) {}
            if (data.getToPlay() != null) {
                if (data.getSpecialMove().equals("Draw")) {
                    game.draw();
                } else {
                    game.playCard(data.getToPlay());
                    game.doSpecialMove(data.getSpecialMove());
                }
            }
        }


        try {
            logger.log(Level.INFO, "Closing socket");
            servsoc.close();
            soc.close();
            dout.close();
            din.close();
        } catch (IOException ignored) {}
    }

    // Methods to read and write JSON to and from the data stream
    private void writeJSON(UnoNetData toJSON) throws IOException {
//        logger.log(Level.INFO, "Writing to data stream");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(dout, toJSON);
    }

    private void readJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.readValue(din, UnoNetData.class);
    }

    private JPanel waitConn() {
        // Creates panel to show that the server is waiting for a valid connection to start the game
        JPanel waitConn = new JPanel(new BorderLayout());
        JTextArea waiting = new JTextArea("Waiting for clients");
        waiting.setEditable(false);
        waiting.setFont(new Font(ARIAL, Font.PLAIN, 75));
        waitConn.add(waiting, BorderLayout.CENTER);

        return waitConn;
    }

    private JPanel gameMenu() {
        // Creates panel for when the game is running and displays info about the game
        JPanel gameMenu = new JPanel(new GridLayout(listener.getPlayerCount() + 3, 1));
        gameMenu.setBackground(none);
        JLabel infoTxt = new JLabel("Game Info");
        infoTxt.setFont(new Font(ARIAL, Font.PLAIN, 50));
        JPanel infoTxtPn = new JPanel();
        infoTxtPn.add(infoTxt, BorderLayout.CENTER);
        infoTxtPn.setBackground(none);
        JButton menu = new JButton("Menu");
        menu.setFont(new Font(ARIAL, Font.PLAIN, 50));
        JPanel veryTop = new JPanel(new GridLayout(1, 2));
        veryTop.add(infoTxtPn);
        veryTop.add(menu);
        gameMenu.add(veryTop);

        targetHeight = dims.getHeight() / (listener.getPlayerCount() + 4);
        targetWidth = targetHeight / 100 * 120; //TODO Maybe fix ratio
        Image img = game.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        JLabel placePile = new JLabel(new ImageIcon(img));
        JLabel cardsLeft = new JLabel("Cards left: " + game.getCardsLeft());
        cardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 50));
        drawCardsLeft.add(cardsLeft);

        JPanel cardsLeftPn = new JPanel();
        cardsLeftPn.add(drawCardsLeft.get(0), BorderLayout.CENTER);
        cardsLeftPn.setBackground(none);
        JPanel cardInfo = new JPanel(new GridLayout(1, 2));
        cardInfo.add(placePile);
        cardInfo.add(cardsLeftPn);
        cardInfo.setBackground(none);
        gameMenu.add(cardInfo);

        StringBuilder cardsLeftAsString = new StringBuilder("Cards left:  ");
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(game.getPCardsLeft(i));
            if (i < listener.getPlayerCount() - 1) {
                cardsLeftAsString.append(" - ");
            }
        }
        JLabel pCardsLeft = new JLabel(cardsLeftAsString.toString());
        pCardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 40));
        playerCardsLeft.add(pCardsLeft);
        JPanel playerCardsLeftPn = new JPanel();
        playerCardsLeftPn.add(playerCardsLeft.get(0), BorderLayout.CENTER);
        playerCardsLeftPn.setBackground(none);
        gameMenu.add(playerCardsLeftPn);
        // Player Cards
        for (int i = 0; i < data.getPlayerCount(); i++) {
            pCards.add(initialSet(i));
            gameMenu.add(pCards.get(i));
        }

        return gameMenu;
    }

    private JPanel initialSet(int player) {
        // Creates the initial version of the player's cards
        JPanel initialCards = new JPanel();
        Hand playerHand = data.getHand(player);
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        for (int i = 0; i < playerHand.length(); i++) {
            JLabel card = new JLabel(playerHand.getCard(i).toString());
            Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            card.setIcon(new ImageIcon(img));
            card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
            card.setSize(targetWidth, targetHeight);
            initialCards.add(card);
        }

        return initialCards;
    }

    public void updatePlayerCards(int player) {
        JPanel newCards = pCards.get(player);
        Hand playerHand = data.getHand(player);
        boolean bigger13 = false;
        int div = 20;
        if (playerHand.length() > 13) {
            bigger13 = true;
            div += (playerHand.length() / 7) * playerHand.length();
        }
        targetWidth = dims.getWidth() / div;
        targetHeight = targetWidth * 143 / 100;
        if (bigger13) {
            newCards = new JPanel();
            for (int i = 0; i < playerHand.length(); i++) {
                JLabel card = new JLabel(playerHand.getCard(i).toString());
                Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                card.setIcon(new ImageIcon(img));
                card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
                card.setSize(targetWidth, targetHeight);
                newCards.add(card);
            }
        } else {
            int amtNewCards = 0;//listener.newCardsAdded();
            if (amtNewCards > 0) {
                for (int i = amtNewCards; i > 0; i--) {
                    JLabel card = new JLabel(playerHand.getCard(playerHand.length() - i).toString());
                    Image img = playerHand.getCard(playerHand.length() - i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    card.setIcon(new ImageIcon(img));
                    card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
                    card.setSize(targetWidth, targetHeight);
                    newCards.add(card);
                }
            }
        }
        if (playerHand.length() <= 13) {
            bigger13 = false;
        }

        pCards.set(player, newCards);
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
