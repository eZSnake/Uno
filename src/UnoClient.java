import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UnoClient extends JPanel {
    private static JFrame window;
    private static PlayerListener listener;
    private static UnoNetData data;
    private Socket soc;
    private InputStreamReader din;
    private OutputStreamWriter dout;
    private static int player = 0, numPlayers, targetWidth, targetHeight, port;
    private static Image back;
    private static Container c;
    private CardLayout screen = new CardLayout();
    private final PanelDims dims = new PanelDims(1920, 1080);
    private JLabel pCardsLeft, cardsLeft, placePile;
    private ArrayList<JPanel> botCards = new ArrayList<>(), pCards = new ArrayList<>();
    private ArrayList<JLabel> selectedPlayer = new ArrayList<>(), playerCardsLeft = new ArrayList<>(), drawCardsLeft = new ArrayList<>(), placePileCard = new ArrayList<>();
    private static final String tab = "    ", ARIAL = "Arial";
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main(String[] args) {
        port = 4200;
        data = new UnoNetData(null, null, null, -1, -1, -1, -1, null);

        window = new JFrame("Uno");
        UnoClient client = new UnoClient();
        listener = new PlayerListener(client);
        window.setSize(1920,1080);

        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        c = window.getContentPane();
        c.setLayout(client.screen);
        c.add(client.waitingScreen());
        c.add(client.waitForGame());

        window.setContentPane(c);
        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    private JPanel selection(int amtPlayers) {
        // Creates the panel where the player can select their player number
        numPlayers = amtPlayers;
        JPanel selection = new JPanel();
        selection.setLayout(new GridLayout(numPlayers + 3, 1)); //TODO Only have enough slots for required amt of players
        JTextArea topTxt = new JTextArea("\nSelect which player you would like to be");
        topTxt.setFont(new Font(ARIAL, Font.PLAIN, 50));
        topTxt.setEditable(false);
        JPanel topTxtBox = new JPanel();
        topTxtBox.add(topTxt);
        topTxtBox.setBackground(none);
        selection.add(topTxtBox);
        for (int i = 0; i < numPlayers; i++) {
            JButton tempButton = new JButton("Player " + (i + 1));
            tempButton.setFont(new Font(ARIAL, Font.PLAIN, 35));
            tempButton.addActionListener(listener);
            selection.add(tempButton);
        }

        JLabel botTxt = new JLabel("Currently player " + player);
        botTxt.setFont(new Font(ARIAL, Font.PLAIN, 50));
        selectedPlayer.add(botTxt);
        JPanel botTxtBox = new JPanel();
        botTxtBox.add(selectedPlayer.get(0));
        botTxtBox.setBackground(none);
        selection.add(botTxtBox);
        JButton cont = new JButton("Continue");
        cont.setFont(new Font(ARIAL, Font.PLAIN, 35));
        cont.addActionListener(listener);
        selection.add(cont);

        return selection;
    }

    private JPanel playerPlayingScreen(int player) {
        // Creates playing screen for specified player
        JPanel playerPlayingScreen = new JPanel();
        playerPlayingScreen.setLayout(new BorderLayout());
        // Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        top.setBackground(none);
        // Left top (cards left and go to menu)
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(3, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        left.add(goMenu);
        StringBuilder cardsLeftAsString = new StringBuilder("Cards left:  ");
        for (int i = 0; i < data.getPlayerCount(); i++) {
            cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(data.getCardsLeft(i));
            if (i < data.getPlayerCount() - 1) {
                cardsLeftAsString.append(" - ");
            }
        }
        pCardsLeft = new JLabel(cardsLeftAsString.toString());
        pCardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerCardsLeft.add(pCardsLeft);
        JPanel centPlayerCardsLeft = new JPanel();
        centPlayerCardsLeft.add(playerCardsLeft.get(player), BorderLayout.CENTER);
        centPlayerCardsLeft.setBackground(none);
        left.add(centPlayerCardsLeft);
        JLabel currPlayer = new JLabel(tab + tab + "Current player: " + (player + 1));
        currPlayer.setFont(new Font(ARIAL, Font.PLAIN, 20));
        left.add(currPlayer);
        left.setBackground(none);
        top.add(left);
        // Top right (cards left in draw pile w/ pic and debug print button)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(3, 1));
        JButton printHands = new JButton("Print Hands");
        printHands.addActionListener(listener);
        printHands.setFont(new Font(ARIAL, Font.PLAIN, 30));
        right.add(printHands);
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        right.add(new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH))));
        cardsLeft = new JLabel("Cards in drawpile: " + data.getCardsInDrawPile());
        cardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        drawCardsLeft.add(cardsLeft);
        JPanel centDrawCardsLeft = new JPanel();
        centDrawCardsLeft.add(drawCardsLeft.get(player), BorderLayout.CENTER);
        centDrawCardsLeft.setBackground(none);
        right.add(centDrawCardsLeft);
        right.setBackground(none);
        top.add(right);
        playerPlayingScreen.add(top, BorderLayout.NORTH);
        // Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = data.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        placePileCard.add(placePile);
        playerPlayingScreen.add(placePileCard.get(player), BorderLayout.CENTER);
        // Bottom (cards on hand and draw)
        JPanel bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);
        draw.setFont(new Font(ARIAL, Font.BOLD, 40));
        pCards.add(initialSet(player));
        bottomCards.add(pCards.get(player));
        bottomCards.add(draw);
        bottomCards.setBackground(none);
        botCards.add(bottomCards);
        playerPlayingScreen.add(botCards.get(player), BorderLayout.SOUTH);
        playerPlayingScreen.setBackground(none);

        return playerPlayingScreen;
    }

    private JPanel initialSet(int player) {
        // Creates the initial version of the player's cards
        JPanel initialCards = new JPanel();
        Hand playerHand = data.getHand(player);
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        for (int i = 0; i < playerHand.length(); i++) {
            JButton card = new JButton(playerHand.getCard(i).toString());
            Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            card.setIcon(new ImageIcon(img));
            card.addActionListener(listener);
            card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
            card.setSize(targetWidth, targetHeight);
            initialCards.add(card);
        }

        return initialCards;
    }

    public void updateCards() {
        setPanelDims(window.getWidth(), window.getHeight());
        JPanel toUpdate = botCards.get(player);
        toUpdate.setVisible(false);
        toUpdate.removeAll();
        toUpdate.setLayout(new GridLayout(2, 1));
        updatePlayerCards(player);
        toUpdate.setBackground(none);
        toUpdate.add(pCards.get(player));
        if (data.getCardsLeft(player) != 0) {
            JButton draw = new JButton("Draw");
            draw.setFont(new Font(ARIAL, Font.BOLD, 40));
            draw.addActionListener(listener);
            toUpdate.add(draw);
        }
        toUpdate.setVisible(true);
        repaint();
    }

//    private void removeCard(int player) {
//        int toRemove = listener.getPlayedCard();
//        if (toRemove != -1) {
//            pCards.get(player).remove(toRemove);
//            listener.removeCard();
//        }
//    }

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
                JButton card = new JButton(playerHand.getCard(i).toString());
                Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                card.setIcon(new ImageIcon(img));
                card.addActionListener(listener);
                card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
                card.setSize(targetWidth, targetHeight);
                newCards.add(card);
            }
        } else {
            int amtNewCards = 0;//listener.newCardsAdded();
            if (amtNewCards > 0) {
                for (int i = amtNewCards; i > 0; i--) {
                    JButton card = new JButton(playerHand.getCard(playerHand.length() - i).toString());
                    Image img = playerHand.getCard(playerHand.length() - i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    card.setIcon(new ImageIcon(img));
                    card.addActionListener(listener);
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

    public void updateWholeScreen() {

    }

    public void removeCard(Card toRemove) {

    }

    private JPanel waitingScreen() {
        // Creates a waiting screen to show the game is waiting for a connection
        JPanel wait = new JPanel(new GridLayout(3, 1));
        JLabel serverInfo = new JLabel("Attempting to connect on IP 192.168.201.1 and Port " + port);
        JPanel servInf = new JPanel();
        servInf.add(serverInfo);
        wait.add(servInf);
        JLabel waitTxt = new JLabel("Waiting for server connection...");
        waitTxt.setFont(new Font(ARIAL, Font.PLAIN, 75));
        JPanel waitTxtScrn = new JPanel();
        waitTxtScrn.add(waitTxt);
        wait.add(waitTxtScrn);
        JButton connect = new JButton("Start connection");
        connect.addActionListener(listener);
        wait.add(connect);

        return wait;
    }

    private JPanel waitForGame() {
        // Creates a waiting screen while the server hasn't created the game yet
        JPanel waitForGame = new JPanel(new BorderLayout());
        waitForGame.setBackground(none);
        JLabel waiting = new JLabel("Waiting for game to start");
        waiting.setFont(new Font(ARIAL, Font.PLAIN, 75));
        waitForGame.add(waiting);

        return waitForGame;
    }

    private JPanel winnerScreen(int winner) {
        // Screen to show which player won
        JPanel winnerScreen = new JPanel(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        winnerScreen.add(goMenu, BorderLayout.NORTH);
        JTextArea text = new JTextArea("Player " + (winner + 1) + " wins!\nCongratulations!\nBut can this feat be repeated?");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        winnerScreen.add(winTxt);

        return winnerScreen;
    }

    private JPanel tieGame() {
        // Screen to show that the game resulted in a tie
        JPanel tieGame = new JPanel();
        tieGame.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        tieGame.add(goMenu, BorderLayout.NORTH);
        JTextArea text = new JTextArea("The game is a tie!\nThere are no more cards to draw or play!\nBetter luck next time!");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        tieGame.add(winTxt);

        return tieGame;
    }

    public void startConnection() {
        // Starts the connection to the server
        boolean connected = false;
        while (!connected) {
            try {
                System.out.println("Attempting connection");
                soc = new Socket("localhost", port);
                din = new InputStreamReader(soc.getInputStream());
                dout = new OutputStreamWriter(soc.getOutputStream());
                System.out.println("Connection success");
            } catch (IOException ignored) {}
            connected = true;
        }

        screen.next(c);

        while (data.getPlayerCount() == -1) {
            try {
                readJSON();
            } catch (IOException ignored) {}
        }

        c.add(selection(data.getPlayerCount()));
        screen.next(c);
    }

    // Methods to read and write JSON to and from the data stream
    private void writeJSON(UnoNetData toJSON) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(dout, toJSON);
    }

    private void readJSON() throws IOException {
//        System.out.println("Reading from datastream\n" + din.toString());
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.readValue(din, UnoNetData.class);
    }

    public void playGame() {
        // Code that runs the game until it ends
        while (data.getWinner() == -1) {
            //accept and send data
            try {
                readJSON();
            } catch (IOException ignored) {}
        }

        int winner = data.getWinner();
        if (winner == 4) {
            c.add(tieGame());
        } else {
            c.add(winnerScreen(winner));
        }
        screen.next(c);

        try {
            soc.close();
            dout.close();
            din.close();
        } catch(IOException ignored) {}
    }

    public void playCard(Card toPlay, String specialMove) {
        UnoNetData toSend = new UnoNetData(null, toPlay, null, -1, -1, -1, -1, specialMove);

        try {
            writeJSON(toSend);
        } catch (IOException ignored) {}
    }

    public void draw() {
        playCard(null, "draw");
    }

    public Hand getHand() {
        return data.getHand(player);
    }

    public Card getPlacePile() {
        return data.getPlacePile();
    }

    public void setPlayer(int newPlayer) {
        player = newPlayer - 1;
        selectedPlayer.get(0).setText("Currently player " + (player + 1));
    }

    public int getCurrPlayer() {
        return data.getCurrPlayer();
    }

    public void setPlayerScreen() {
        c.add(playerPlayingScreen(player));
    }

    public void nextScreen() {
        screen.next(c);
    }

    public void goMenu() {
        c.setVisible(false);
        c.removeAll();
        c.add(waitingScreen(), "wait");
        c.add(selection(data.getPlayerCount()), "menu");
        c.setVisible(true);
    }

    private void setPanelDims(int width, int height) {
        targetHeight = height;
        targetWidth = width;
    }
}
