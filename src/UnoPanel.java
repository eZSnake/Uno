import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UnoPanel extends JPanel {
    private static Container c;
    private static CardLayout screen;
    private static PanelDims dims;
    private JLabel pCardsLeft, cardsLeft, placePile, botsPlay;
    private static UnoListener listener;
    private JPanel cards;
    private ArrayList<JPanel> botCards = new ArrayList<>();
    private ArrayList<JLabel> playerCardsLeft = new ArrayList<>(), drawCardsLeft = new ArrayList<>(), placePileCard = new ArrayList<>();
    private static final String tab = "    ", ARIAL = "Arial";
    private static int targetWidth, targetHeight;
    private static Image back;
    private static final Color none = new Color(255, 255, 255, 200);

    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        listener = new UnoListener(panel);
        window.setSize(1920,1080);
        dims = new PanelDims(window.getWidth(), window.getHeight());

        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}

        screen = new CardLayout();
        c = window.getContentPane();
        c.setLayout(screen);
        c.add(menu());
        window.setContentPane(c);

        window.setLocation(0,0);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public void setBotGame() {
        //Adds single player screen to container to play against bot
        c.add(botPlayingScreen());
        c.add(playerBotWins(), "playerWin");
        c.add(botWins(), "botWin");
        c.add(tieGame(), "tieGame");
    }

    public void setPlayerGame() {
        //Adds player screens to the container to play against others
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            c.add(playerPlayingScreen(i), "player" + i);
            c.add(playerNWins(i), "player" + i + "Win");
        }
        c.add(tieGame(), "tieGame");
    }

    public void resetC() {
        //Resets container
        c.removeAll();
        c.add(menu());
        botCards.clear();
        playerCardsLeft.clear();
        drawCardsLeft.clear();
        placePileCard.clear();
    }

    public static JPanel menu() {
        //Creates menu screen
        JPanel menu = new JPanel();
        menu.setLayout(new BorderLayout());
        //Choose if you play against bot or real people
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(3, 1));
        JButton bot = new JButton("Bot");
        bot.addActionListener(listener);
        bot.setFont(new Font(ARIAL, Font.PLAIN, 20));
        buttons.add(bot);
        JPanel player = new JPanel();
        player.setLayout(new GridLayout(1, 2));
        JButton players = new JButton("Players: ");
        players.addActionListener(listener);
        players.setFont(new Font(ARIAL, Font.PLAIN, 20));
        player.add(players);
        JSlider playerCount = new JSlider(2, 4, 2);
        playerCount.addChangeListener(listener);
        playerCount.setMajorTickSpacing(1);
        playerCount.setPaintTicks(true);
        player.add(playerCount);
        buttons.add(player);
        JTextArea info = new JTextArea("The game takes a bit to start. Pressing the button again makes you draw a card right away. So don't do it.");
        info.setEditable(false);
        info.setFont(new Font(ARIAL, Font.ITALIC, 15));
        info.setForeground(Color.RED);
        JPanel infoPan = new JPanel();
        infoPan.add(info, BorderLayout.CENTER);
        infoPan.setBackground(none);
        buttons.add(infoPan);
        menu.add(buttons, BorderLayout.SOUTH);
        //Welcome text at top of screen
        JTextArea welcome = new JTextArea("Welcome to the game of Uno.\n" + "Each player starts with 7 cards and first with 0 left wins.\n" + "The same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\n" + "If you can't go, you will have to draw a card.");
        welcome.setFont(new Font(ARIAL, Font.PLAIN, 20));
        welcome.setEditable(false);
        JPanel welcomePan = new JPanel();
        welcomePan.add(welcome, BorderLayout.CENTER);
        welcomePan.setBackground(none);
        menu.add(welcomePan, BorderLayout.NORTH);
        //Image of back of Uno card at center
        targetWidth = dims.getWidth() / 5;
        targetHeight = targetWidth * 143 / 100;
        JLabel picLabel = new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)));
        menu.add(picLabel, BorderLayout.CENTER);

        return menu;
    }

    public JPanel botPlayingScreen() {
        //Creates screen for a player to play against the bot
        JPanel botPlayingScreen = new JPanel();
        botPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        //Left top (cards left and go to menu
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(3, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        left.add(goMenu);
        pCardsLeft = new JLabel("\nCards left:  Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        pCardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerCardsLeft.add(pCardsLeft);
        JPanel centPlayerCardsLeft = new JPanel();
        centPlayerCardsLeft.add(playerCardsLeft.get(0), BorderLayout.CENTER);
        left.add(centPlayerCardsLeft);
        botsPlay = new JLabel(tab + tab + "Bot's play: " + listener.botsPlay());
        botsPlay.setFont(new Font(ARIAL, Font.PLAIN, 20));
        left.add("botsplay", botsPlay);
        top.add(left);
        //Top right (cards left in draw pile w/ pic)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1));
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        right.add(new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH))));
        cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft());
        cardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        drawCardsLeft.add(cardsLeft);
        JPanel centDrawCardsLeft = new JPanel();
        centDrawCardsLeft.add(drawCardsLeft.get(0), BorderLayout.CENTER);
        right.add(centDrawCardsLeft);
        top.add(right);
        botPlayingScreen.add(top, BorderLayout.NORTH);
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        placePileCard.add(placePile);
        botPlayingScreen.add(placePileCard.get(0), BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
        JPanel bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.setFont(new Font(ARIAL, Font.BOLD, 40));
        draw.addActionListener(listener);
        cards = playerCards(0);
        bottomCards.add(cards);
        bottomCards.add(draw);
        botCards.add(bottomCards);
        botPlayingScreen.add(botCards.get(0), BorderLayout.SOUTH);

        return botPlayingScreen;
    }

    public JPanel playerPlayingScreen(int player) {
        //Creates playing screen for specified player
        System.out.println("Setting layout for player " + player);
        JPanel playerPlayingScreen = new JPanel();
        playerPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        //Left top (cards left and go to menu
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(3, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        left.add(goMenu);
        StringBuilder cardsLeftAsString = new StringBuilder("Cards left:  ");
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(listener.pCardsLeft(i));
            if (i < listener.getPlayerCount() - 1) {
                cardsLeftAsString.append(" - ");
            }
        }
        pCardsLeft = new JLabel(cardsLeftAsString.toString());
        pCardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerCardsLeft.add(pCardsLeft);
        JPanel centPlayerCardsLeft = new JPanel();
        centPlayerCardsLeft.add(playerCardsLeft.get(player), BorderLayout.CENTER);
        left.add(centPlayerCardsLeft);
        JLabel currPlayer = new JLabel(tab + tab + "Current player: " + (player + 1));
        currPlayer.setFont(new Font(ARIAL, Font.PLAIN, 20));
        left.add(currPlayer);
        top.add(left);
        //Top right (cards left in draw pile w/ pic)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1));
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        right.add(new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH))));
        cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft());
        cardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        drawCardsLeft.add(cardsLeft);
        JPanel centDrawCardsLeft = new JPanel();
        centDrawCardsLeft.add(drawCardsLeft.get(player), BorderLayout.CENTER);
        right.add(centDrawCardsLeft);
        top.add(right);
        playerPlayingScreen.add(top, BorderLayout.NORTH);
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        placePileCard.add(placePile);
        playerPlayingScreen.add(placePileCard.get(player), BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
        JPanel bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);
        draw.setFont(new Font(ARIAL, Font.BOLD, 40));
        cards = playerCards(player);
        bottomCards.add(cards);
        bottomCards.add(draw);
        botCards.add(bottomCards);
        playerPlayingScreen.add(botCards.get(player), BorderLayout.SOUTH);

        return playerPlayingScreen;
    }

    public JPanel playerCards(int player) {
        JPanel cards = new JPanel();
        int div = 20;
        Hand playerHand = listener.getPlayerHand(player);
        //TODO Optimize size changeing!!!
        if (playerHand.length() > 13) {
            div += (playerHand.length() / 7) * playerHand.length();
        }
        targetWidth = dims.getWidth() / div;
        targetHeight = targetWidth * 143 / 100;
        //TODO Only remove card that was played and add new cards
        for (int i = 0; i < playerHand.length(); i++) {
            JButton card = new JButton(playerHand.getCard(i).toString());
            Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            card.setIcon(new ImageIcon(img));
            card.addActionListener(listener);
            card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
            card.setSize(targetWidth, targetHeight);
            cards.add(card);
        }

        return cards;
    }

    public void updateCardElements() {
        pCardsLeft = playerCardsLeft.get(listener.getPlayer());
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            if (listener.pCardsLeft(i) <= 3) {
                pCardsLeft.setForeground(Color.RED);
                break;
            } else {
                pCardsLeft.setForeground(Color.BLACK);
            }
        }
        if (listener.isBotGame()) {
            pCardsLeft.setText("Cards left:  Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
            botsPlay.setText(tab + tab + "Bot's play: " + listener.botsPlay());
        } else {
            StringBuilder cardsLeftAsString = new StringBuilder(tab + tab + "Cards left:  ");
            for (int i = 0; i < listener.getPlayerCount(); i++) {
                cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(listener.pCardsLeft(i));
                if (i < listener.getPlayerCount() - 1) {
                    cardsLeftAsString.append(" - ");
                }
            }
            pCardsLeft.setText(cardsLeftAsString.toString());
        }
        cardsLeft = drawCardsLeft.get(listener.getPlayer());
        cardsLeft.setText(tab + tab + "Cards in drawpile: " + listener.getCardsLeft());

        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        placePile = placePileCard.get(listener.getPlayer());
        placePile.setIcon(new ImageIcon(listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)));
        repaint();
    }
    //TODO Maybe refresh only elements when bot plays and everything when player plays
    public void updateCards(int player) {
        JPanel toUpdate = botCards.get(player);
        toUpdate.setVisible(false);
//        toUpdate.remove(cards); //makes everything very wonky
        toUpdate.removeAll();
        toUpdate.setLayout(new GridLayout(2, 1));
        cards = playerCards(player);
        toUpdate.add(cards);
        JButton draw = new JButton("Draw");
        draw.setFont(new Font(ARIAL, Font.BOLD, 40));
        draw.addActionListener(listener);
        toUpdate.add(draw);
        toUpdate.setVisible(true);
        repaint();
    }

    public static JPanel playerBotWins() {
        JPanel playerBotWins = new JPanel();
        playerBotWins.setLayout(new BoxLayout(playerBotWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        playerBotWins.add(goMenu);
        JTextArea text = new JTextArea("You win!\nCongratulations!\nBut can you do it again?");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        playerBotWins.add(winTxt);

        return playerBotWins;
    }

    public static JPanel playerNWins(int player) {
        JPanel playerNWins = new JPanel();
        playerNWins.setLayout(new BoxLayout(playerNWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        playerNWins.add(goMenu);
        JTextArea text = new JTextArea("Player " + (player + 1) + " wins!\nCongratulations!\nBut can this feat be repeated?");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        playerNWins.add(winTxt);

        return playerNWins;
    }

    public static JPanel botWins() {
        JPanel botWins = new JPanel();
        botWins.setLayout(new BoxLayout(botWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        botWins.add(goMenu);
        JTextArea text = new JTextArea("The bot wins!\nBetter luck next time!\n*Robot Noises*");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        botWins.add(winTxt);
        Image robot = null;
        targetWidth = dims.getWidth() / 8;
        targetHeight = targetWidth * 143 / 100;
        try {
            robot = ImageIO.read(new File("UnoCards/robot.jpg")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        botWins.add(new JLabel(new ImageIcon(robot)));

        return botWins;
    }

    public static JPanel tieGame() {
        JPanel tieGame = new JPanel();
        tieGame.setLayout(new BoxLayout(tieGame, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        tieGame.add(goMenu);
        JTextArea text = new JTextArea("The game is a tie!\nThere are no more cards to draw or play!\nBetter luck next time!");
        text.setFont(new Font(ARIAL, Font.PLAIN, 50));
        text.setEditable(false);
        JPanel winTxt = new JPanel();
        winTxt.add(text, BorderLayout.CENTER);
        winTxt.setBackground(none);
        tieGame.add(winTxt);

        return tieGame;
    }

    public void nextScreen() {
        screen.next(c);
    }

    public void goToMenu() {
        screen.first(c);
    }

    public void showScreen(String toShow) {
        screen.show(c, toShow);
    }

    public void playerScreen(String player) {
        screen.show(c, player);
    }

    public void setPanelDims(int width, int height) {
        dims.setWidth(width);
        dims.setHeight(height);
    }
}
