import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class UnoPanel extends JPanel {
    private static Container c;
    private static CardLayout screen;
    private static PanelDims dims;
    private static JLabel pCardsLeft, cardsLeft, placePile;
    private static UnoListener listener;
    private static JPanel cards, bottomCards;

    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        listener = new UnoListener(panel);
        window.setSize(1920,1080);
        dims = new PanelDims(window.getWidth(), window.getHeight());

        screen = new CardLayout();
        c = window.getContentPane();
        c.setLayout(screen);
        c.add(menu());
        window.setContentPane(c);

        window.setLocation(300,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public void setBotGame() {
        //Adds single player screen to container to paly against bot
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
        buttons.add(bot);
        JPanel player = new JPanel();
        player.setLayout(new GridLayout(1, 2));
        JButton players = new JButton("Players: ");
        players.addActionListener(listener);
        player.add(players);
        JSlider playerCount = new JSlider(2, 4, 2);
        playerCount.addChangeListener(listener);
        playerCount.setMajorTickSpacing(1);
        playerCount.setPaintTicks(true);
        player.add(playerCount);
        buttons.add(player);
        menu.add(buttons, BorderLayout.SOUTH);
        //Welcome text at top of screen
        JTextArea welcome = new JTextArea("Welcome to the game of Uno.\nEach player starts with 7 cards and first with 0 left wins.\nThe same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\nIf you can't go, you draw a card.");
        welcome.setFont(new Font("Arial", Font.PLAIN, 20));
        welcome.setEditable(false);
        welcome.setAlignmentX(500);
        menu.add(welcome, BorderLayout.NORTH);
        //Image of back of Uno card at center
        Image back = null;
        int targetWidth = dims.getWidth() / 5, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        JLabel picLabel = new JLabel(new ImageIcon(back));
        menu.add(picLabel, BorderLayout.CENTER);
        return menu;
    }

    public static JPanel botPlayingScreen() {
        //Creates screen for a player to play against the bot
        JPanel botPlayingScreen = new JPanel();
        botPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        //Left top (cards left and go to menu
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(2, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        left.add(goMenu);
        pCardsLeft = new JLabel("        Cards left: Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        pCardsLeft.setFont(new Font("Arial", Font.PLAIN, 20));
        left.add("playercards", pCardsLeft);
        top.add(left);
        //Top right (cards left in draw pile w/ pic)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1));
        Image back = null;
        int targetWidth = dims.getWidth() / 20, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        right.add(new JLabel(new ImageIcon(back)));
        cardsLeft = new JLabel("        Cards in drawpile: " + listener.getCardsLeft());
        cardsLeft.setFont(new Font("Arial", Font.PLAIN, 20));
        right.add("cardsleft", cardsLeft);
        top.add(right);
        botPlayingScreen.add(top, BorderLayout.NORTH);
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        botPlayingScreen.add(placePile, BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
        bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);
        cards = playerCards(0);
        bottomCards.add(cards);
        bottomCards.add(draw);
        botPlayingScreen.add(bottomCards, BorderLayout.SOUTH);

        return botPlayingScreen;
    }

    public static JPanel playerPlayingScreen(int player) {
        //Creates playing screen for specified player
        JPanel playerPlayingScreen = new JPanel();
        playerPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        //Left top (cards left and go to menu
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(2, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        left.add(goMenu);
        StringBuilder cardsLeftAsString = new StringBuilder("        Cards left: ");
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(listener.pCardsLeft(i));
            if (i < listener.getPlayerCount() - 1) {
                cardsLeftAsString.append(" - ");
            }
        }
        pCardsLeft = new JLabel(cardsLeftAsString.toString());
        pCardsLeft.setFont(new Font("Arial", Font.PLAIN, 20));
        left.add("playercards", pCardsLeft);
        top.add(left);
        //Top right (cards left in draw pile w/ pic)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1));
        Image back = null;
        int targetWidth = dims.getWidth() / 20, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        right.add(new JLabel(new ImageIcon(back)));
        cardsLeft = new JLabel("        Cards in drawpile: " + listener.getCardsLeft());
        cardsLeft.setFont(new Font("Arial", Font.PLAIN, 20));
        right.add("cardsleft", cardsLeft);
        top.add(right);
        playerPlayingScreen.add(top, BorderLayout.NORTH);
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        playerPlayingScreen.add(placePile, BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
        bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);
        cards = playerCards(player);
        bottomCards.add(cards);
        bottomCards.add(draw);
        playerPlayingScreen.add(bottomCards, BorderLayout.SOUTH);

        return playerPlayingScreen;
    }

    public static JPanel playerCards(int player) {
        //Sets cards on hand for specified player
        JPanel cards = new JPanel();
        int div = 20;
        Hand playerHand = listener.getPlayerHand(player);
        //TODO Optimize size changeing
        if (playerHand.length() > 14) {
            div += 2 * playerHand.length() + 4;
        }
        int targetWidth = dims.getWidth() / div, targetHeight = targetWidth * 143 / 100;
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
//        System.out.println("Updating card elements");
        if (listener.isBotGame()) {
            pCardsLeft.setText("        Cards left:  Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        } else {
            StringBuilder cardsLeftAsString = new StringBuilder("        Cards left:  ");
            for (int i = 0; i < listener.getPlayerCount(); i++) {
                cardsLeftAsString.append("Player ").append(i + 1).append(": ").append(listener.pCardsLeft(i));
                if (i < listener.getPlayerCount() - 1) {
                    cardsLeftAsString.append(" - ");
                }
            }
            pCardsLeft.setText(cardsLeftAsString.toString());
        }
        cardsLeft.setText("        Cards in drawpile: " + listener.getCardsLeft());

        int targetWidth = dims.getWidth() / 15, targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile.setIcon(new ImageIcon(img));
        repaint();
    }
    //TODO Maybe refresh only elements when bot plays and everything when player plays
    public void updateCards(int player) {
        //TODO Card overflow when having over 9 cards on hand
        bottomCards.removeAll();
        bottomCards.setLayout(new GridLayout(2, 1));
        cards = playerCards(player);
        bottomCards.add(cards);
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);
        bottomCards.add(draw);
        repaint();
    }

    public static JPanel allCards() {
        JPanel cards = new JPanel();
        Deck deck = listener.getDeck();
        for (Card card : deck.getCards()) {
            Image img = card.getImage().getScaledInstance(100, 143, Image.SCALE_SMOOTH);
            JLabel image = new JLabel(new ImageIcon(img));
            cards.add(image);
//            System.out.println(card);
        }
        return cards;
    }

    public static JPanel playerBotWins() {
        JPanel playerBotWins = new JPanel();
        playerBotWins.setLayout(new BoxLayout(playerBotWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        playerBotWins.add(goMenu);
        JTextArea text = new JTextArea("        You win!\n        Congratulations!\n        But can you do it again?");
        text.setFont(new Font("Arial", Font.PLAIN, 50));
        text.setEditable(false);
        playerBotWins.add(text);
        return playerBotWins;
    }

    public static JPanel playerNWins(int player) {
        JPanel playerNWins = new JPanel();
        playerNWins.setLayout(new BoxLayout(playerNWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        playerNWins.add(goMenu);
        JTextArea text = new JTextArea("        Player " + (player + 1) + " wins!\n        Congratulations!\n        But can this feat be repeated?");
        text.setFont(new Font("Arial", Font.PLAIN, 50));
        text.setEditable(false);
        playerNWins.add(text);
        return playerNWins;
    }

    public static JPanel botWins() {
        JPanel botWins = new JPanel();
        botWins.setLayout(new BoxLayout(botWins, BoxLayout.Y_AXIS));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        botWins.add(goMenu);
        JTextArea text = new JTextArea("        The bot wins!\n        Better luck next time!\n        *Robot Noises*");
        text.setFont(new Font("Arial", Font.PLAIN, 50));
        text.setEditable(false);
        botWins.add(text);
        Image robot = null;
        int targetWidth = dims.getWidth() / 8, targetHeight = targetWidth * 143 / 100;
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
        JTextArea text = new JTextArea("        The game is a tie!\n        There are no more cards to draw or play!\n        Better luck next time!");
        text.setFont(new Font("Arial", Font.PLAIN, 50));
        text.setEditable(false);
        tieGame.add(text);
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
        System.out.println("Showing " + player + " and updating");
        screen.show(c, player);
//        updateCardElements();
//        updateCards(Integer.parseInt(player.substring(player.length() - 1)));
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }

    public void setPanelDims(int width, int height) {
        dims.setWidth(width);
        dims.setHeight(height);
    }
}
