import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UnoPanel extends JPanel {
    static Container c;
    static CardLayout screen;

    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");

        UnoPanel panel = new UnoPanel();
        UnoListener listener = new UnoListener(panel);

//        window.setContentPane(menu(listener));
//        window.setContentPane(botPlayingScreen(listener));

        screen = new CardLayout();
        c = window.getContentPane();
        c.setLayout(screen);
        c.add(menu(listener));
        c.add(botPlayingScreen(listener));
        window.setContentPane(c);

        window.setSize(800,600);
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
        BufferedImage back = null; //new ImageIcon("UnoCards/back.png").getImage();
        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}
//        int targetWidth = [blank].getWidth() / 4, targetHeight = targetWidth * 143 / 100;
        int targetWidth = 200, targetHeight = targetWidth * 143 / 100;
        Image resultingImage = back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        JLabel picLabel = new JLabel(new ImageIcon(outputImage));
        menu.add(picLabel, BorderLayout.CENTER);
        return menu;
    }

    public static JPanel botPlayingScreen(UnoListener listener) {
        JPanel botPlayingScreen = new JPanel();
        botPlayingScreen.setLayout(new BorderLayout());

        JLabel pCardsLeft = new JLabel("Cards left: Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        botPlayingScreen.add(pCardsLeft, BorderLayout.NORTH);
        Image back = null;
        int targetWidth = 100, targetHeight = targetWidth * 143 / 100;
        try {
            back = ImageIO.read(new File("UnoCards/back.png")).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {}
        botPlayingScreen.add(new JLabel(new ImageIcon(back)), BorderLayout.EAST);
        JLabel cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft() + "    ");
        botPlayingScreen.add(cardsLeft, BorderLayout.EAST);
        JButton draw = new JButton("Draw");
        draw.addActionListener(listener);

        JPanel cards = playerCards(listener);

        JPanel bottom = new JPanel();
//        bottom.add(draw, BorderLayout.SOUTH);
//        bottom.add(cards, BorderLayout.NORTH);
        bottom.setLayout(new GridLayout(2, 1));
        bottom.add(cards);
        bottom.add(draw);

        botPlayingScreen.add(bottom, BorderLayout.SOUTH);

        return botPlayingScreen;
    }

    public static JPanel playerCards(UnoListener listener) {
        JPanel cards = new JPanel();
        int targetWidth = 100, targetHeight = targetWidth * 143 / 100;
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

    public void nextScreen() {
        screen.next(c);
    }
}
