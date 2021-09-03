import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UnoPlayerClient extends JPanel {
    private static JFrame window;
    private static UnoNetListener listener;
    private static UnoNetData data;
    private ObjectInputStream din;
    private ObjectOutputStream dout;
    private static int player = 0, numPlayers, targetWidth, targetHeight, port;
    private static double ip;
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
        UnoPlayerClient client = new UnoPlayerClient();
        listener = new UnoNetListener(client);
        window.setSize(1920,1080);

        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        c = window.getContentPane();
        c.setLayout(client.screen);
        c.add(client.selection(), "menu");
        c.add(client.waitingScreen(), "wait");

        window.setContentPane(c);
        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    private JPanel selection() {
        numPlayers = 4;
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
        //Creates playing screen for specified player
        JPanel playerPlayingScreen = new JPanel();
        playerPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        top.setBackground(none);
        //Left top (cards left and go to menu)
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
        //Top right (cards left in draw pile w/ pic and debug print button)
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
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = data.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        placePileCard.add(placePile);
        playerPlayingScreen.add(placePileCard.get(player), BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
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

        return wait;
    }

    private JPanel winnerScreen(int winner) {
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
        boolean connected = false;
        while (!connected) {
            try {
                Socket soc = new Socket("192.168.201.1", port);
                din = new ObjectInputStream(soc.getInputStream());
                dout = new ObjectOutputStream(soc.getOutputStream());
                connected = true;
            } catch (IOException ignored) {}
        }

        c.add(playerPlayingScreen(player));

        playGame();
    }

    private void playGame() {
        while (data.getWinner() == -1) {
            //accept and send data
        }

        int winner = data.getWinner();
        if (winner == 4) {
            c.add(tieGame());
            screen.next(c);
        } else {
            c.add(winnerScreen(winner));
            screen.next(c);
        }
    }

    public void playCard(Card toPlay, String specialMove) {
        UnoNetData toSend = new UnoNetData(null, toPlay, null, -1, -1, -1, -1, specialMove);

        try {
            dout.flush();
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
        player = newPlayer;
        selectedPlayer.get(0).setText("Currently player " + player);
    }

    public int getPlayer() {
        return player;
    }

    public int getCurrPlayer() {
        return data.getCurrPlayer();
    }

    public void nextScreen() {
        screen.next(c);
    }

    public void goMenu() {
        c.setVisible(false);
        c.removeAll();
        c.add(selection(), "menu");
        c.add(waitingScreen(), "wait");
        c.setVisible(true);
    }

    private void setPanelDims(int width, int height) {
        targetHeight = height;
        targetWidth = width;
    }
}
