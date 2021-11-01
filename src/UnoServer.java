import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.fasterxml.jackson.databind.*;

public class UnoServer extends JPanel {
    private final Logger logger = Logger.getLogger("UnoServer");
    private static JFrame window;
    private static ServerListener listener;
    private ServerSocket serverSoc;
    private Socket soc;
    //private InputStreamReader din;
    private BufferedReader din;
    private OutputStreamWriter dout;
    private UnoGraphicsGame game;
    private UnoNetData data;
    private static Image back;
    private static Container c;
    private static final CardLayout screen = new CardLayout();
    private int targetWidth = 1920, targetHeight = 1080;
    private PanelDims dims = new PanelDims(targetWidth, targetHeight);
    private ArrayList<JPanel> pCards = new ArrayList<>();
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
    
    // Creates initial home screen for the server
    private static JPanel serverPanel() {
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
    
    // Starts up the server and the game, running it until the end
    public void start() {
        c.add(waitConn());
        screen.next(c);
        
        boolean connected = false;
        while (!connected) {
            try {
                logger.log(Level.INFO, "Attempting to open socket");
                serverSoc = new ServerSocket(4200);
                logger.log(Level.INFO, String.format("Socket state: %s", serverSoc));
                logger.log(Level.INFO, "Now waiting for a connection....");
                soc = serverSoc.accept();
                din = new BufferedReader ( new InputStreamReader(soc.getInputStream()));
                dout = new OutputStreamWriter(soc.getOutputStream());
                logger.log(Level.INFO, "We got a connection. Let's see what the client sends our way");
                connected = true;
            } catch (IOException fatalError) {
                logger.log(Level.SEVERE, "A fatal error has occurred.\nConnection to the server could not be established.");
                System.exit(0);
            }
        }
        

        //logger.log(Level.INFO, "Creating game while we are ready for clients to connect");
        // with the accept above we already wait until a clisnt connects so only get here if someone connected
        logger.log(Level.INFO, "Creating game for the client that just connected. We have " + listener.getPlayerCount() + " players.");
        
        game = new UnoGraphicsGame(listener.getPlayerCount());
        c.add(gameMenu());
        
        data = new UnoNetData(game.getPlacePile(), null, game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft(), -1, null);
        logger.log(Level.INFO, "Game created");
        
        screen.next(c);
        
        // while (connected) {
        //     try {
        //         System.out.println("echo: " + din.readLine());
        //     } catch (IOException fatalError) {
        //     }
        // }

        /*
        // Synchs server and client so they can send and receive data appropriately
        synchronized (data) {
            logger.log(Level.INFO, "In synchronized 1");
            
            data.notifyAll();
        }
        synchronized (data) {
            logger.log(Level.INFO, "In synchronized 2");
            
            try {
                data.wait(2000);
            } catch (InterruptedException interruptedException) {
                logger.log(Level.SEVERE, String.format("Error: %s", interruptedException));
                Thread.currentThread().interrupt();
            }
        }
        */
        logger.log(Level.INFO, "Starting game and synchronized");
        while (game.determineWinner() == -1) {
            setPanelDims(window.getWidth(), window.getHeight());
            // Output game data
            data = new UnoNetData(game.getPlacePile(), null, game.getHands(), game.getPlayer(), listener.getPlayerCount(), game.getCardsLeft(), -1, null);
            logger.log(Level.INFO, "Sending game status to client");
            try {
                writeJSON(data);
            } catch (IOException ignored) {}
            logger.log(Level.INFO, "Successfully sent data to client");

            // Take in game data from players
            logger.log(Level.INFO, "Getting data from client");

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
            serverSoc.close();
            soc.close();
            dout.close();
            din.close();
        } catch (IOException ignored) {}
    }
    
    // Method to write JSON to the data stream
    private void writeJSON(UnoNetData toJSON) throws IOException {
//        logger.log(Level.INFO, "Writing to data stream");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(dout, toJSON);
    }
    
    // Method to read JSON from the data stream
    private void readJSON() throws IOException {
        logger.log(Level.INFO, "Reading from socket... ");
        String rawClientMessage = din.readLine();
        logger.log(Level.INFO, "Reead message " + rawClientMessage );
        ObjectMapper mapper = new ObjectMapper();
        //data = mapper.readValue(din, UnoNetData.class);
        data = mapper.readValue(rawClientMessage, UnoNetData.class );// try {
        //     wait(100);
        // } catch (InterruptedException interruptedException) {
        //     logger.log(Level.SEVERE, String.format("Error: %s", interruptedException));
        //     Thread.currentThread().interrupt();
        // }
    }
    
    // Creates panel to show that the server is waiting for a valid connection to start the game
    private JPanel waitConn() {
        JPanel waitConn = new JPanel(new BorderLayout());
        JTextArea waiting = new JTextArea("Waiting for clients");
        waiting.setEditable(false);
        waiting.setFont(new Font(ARIAL, Font.PLAIN, 75));
        waitConn.add(waiting, BorderLayout.CENTER);
        
        return waitConn;
    }
    
    // Creates panel for when the game is running and displays info about the game
    private JPanel gameMenu() {
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
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            pCards.add(initialSet(i));
            gameMenu.add(pCards.get(i));
        }
        
        return gameMenu;
    }
    
    // Creates the initial version of the player's cards
    private JPanel initialSet(int player) {
        JPanel initialCards = new JPanel();
        Hand playerHand = game.getPlayerHand(player);
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
    
    // Updates the display of the given player's cards
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
