import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UnoPanel extends JPanel {
    public static void main(String[] args) {
        JFrame window = new JFrame("Uno");
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        UnoPanel panel = new UnoPanel();
        UnoListener listener = new UnoListener(panel);

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
//        playerCount.setLabelTable(playerCount.createStandardLabels(1));
        playerCount.setPaintTicks(true);
        buttons.add(playerCount);
        container.add(buttons, BorderLayout.SOUTH);
        container.add(new JTextArea("Welcome to the game of Uno.\nEach player starts with 7 cards and first with 0 left wins.\nThe same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\nIf you can't go, you draw a card.\n"), BorderLayout.NORTH);
        BufferedImage back = null; //new ImageIcon("UnoCards/back.png").getImage();
        try {
            back = ImageIO.read(new File("UnoCards/back.png"));
        } catch (IOException ignored) {}
        int targetWidth = 200, targetHeight = 286;
        Image resultingImage = back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        JLabel picLabel = new JLabel(new ImageIcon(outputImage));
        container.add(picLabel, BorderLayout.CENTER);

        window.setContentPane(container);
        window.setSize(800,600);
        window.setLocation(300,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
