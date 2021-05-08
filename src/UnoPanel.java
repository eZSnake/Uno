import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UnoPanel extends JPanel {
    static Container c;
    static CardLayout screen;
    static PanelDims dims;

    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");
        UnoPanel panel = new UnoPanel();
        UnoListener listener = new UnoListener(panel);
        window.setSize(1920,1080);
        dims = new PanelDims(window.getWidth(), window.getHeight());

//        window.setContentPane(menu(listener));
//        window.setContentPane(botPlayingScreen(listener));
//        window.setContentPane(allCards(listener));

        screen = new CardLayout();
        c = window.getContentPane();
        c.setLayout(screen);
        c.add(menu(listener));
        c.add(botPlayingScreen(listener));
        window.setContentPane(c);

        window.setLocation(300,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public static JPanel menu(UnoListener listener) {
        JPanel menu = new JPanel();
        menu.setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        JButton bot = new JButton("Bot");
        bot.addActionListener(listener);
        buttons.add(bot);
        JButton players = new JButton("Players: ");
        players.addActionListener(listener);
        buttons.add(players);
        JSlider playerCount = new JSlider(2, 4, 2);
        playerCount.addChangeListener(listener);
        playerCount.setMajorTickSpacing(1);
        playerCount.setPaintTicks(true);
        buttons.add(playerCount);
        menu.add(buttons, BorderLayout.SOUTH);

        menu.add(new JTextArea("Welcome to the game of Uno.\nEach player starts with 7 cards and first with 0 left wins.\nThe same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\nIf you can't go, you draw a card.\n"), BorderLayout.NORTH);

        Image back = null;
        int targetWidth = dims.getWidth() / 5, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        JLabel picLabel = new JLabel(new ImageIcon(back));
        menu.add(picLabel, BorderLayout.CENTER);
        return menu;
    }

    public static JPanel botPlayingScreen(UnoListener listener) {
        JPanel botPlayingScreen = new JPanel();
        botPlayingScreen.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new GridLayout(2, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        top.add(goMenu);
        JLabel pCardsLeft = new JLabel("Cards left: Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        top.add("playercards", pCardsLeft);
        botPlayingScreen.add(top, BorderLayout.NORTH);

        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1));
        Image back = null;
        int targetWidth = dims.getWidth() / 20, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        right.add(new JLabel(new ImageIcon(back)));
        JLabel cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft() + "    ");
        right.add("cardsleft", cardsLeft);
        botPlayingScreen.add(right, BorderLayout.EAST);

        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);

        JPanel cards = playerCards(listener);

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(2, 1));
        bottom.add(cards);
        bottom.add(draw);

        botPlayingScreen.add(bottom, BorderLayout.SOUTH);

        return botPlayingScreen;
    }

    public static JPanel playerCards(UnoListener listener) {
        JPanel cards = new JPanel();
        int targetWidth = dims.getWidth() / 20, targetHeight = targetWidth * 143 / 100;
        Hand playerHand = listener.getPlayerHand();
        for (int i = 0; i < playerHand.length(); i++) {
            JButton card = new JButton(playerHand.getCard(i).toString());
            Image img = playerHand.getCard(i).getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            card.setIcon(new ImageIcon(img));
            card.addActionListener(listener);
            cards.add(card);
        }
        return cards;
    }

    public static JPanel allCards(UnoListener listener) {
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

    public void nextScreen() {
        screen.next(c);
    }

    public void goToMenu() {
        screen.first(c);
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }
}
