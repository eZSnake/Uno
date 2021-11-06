import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UnoPanel extends JPanel {
    private static JFrame window;
    private static Container c;
    private static CardLayout screen;
    private static PanelDims dims;
    private JLabel pCardsLeft, cardsLeft, placePile, botsPlay;
    private static UnoListener listener;
    private final JPanel blank = new JPanel();
    private static JPanel gameSettings = new JPanel();
    private ArrayList<JPanel> botCards = new ArrayList<>(), pCards = new ArrayList<>();
    private ArrayList<JLabel> playerCardsLeft = new ArrayList<>(), drawCardsLeft = new ArrayList<>(), placePileCard = new ArrayList<>();
    private static int[] statCounts = new int[16]; //0: tot games; 1: bot games; 2: player games; 3: player wins; 4: bot wins; 5: bot game ties; 6: player 1 wins; 7: player 2 wins; 8: player 3 wins; 9: player 3 wins; 10: player game tie; 11: player 1 games; 12: player 2 games; 13: player 3 games; 14: player 4 games
    private static final String tab = "    ", ARIAL = "Arial";
    private static int targetWidth, targetHeight;
    private boolean bigger13 = false, stackChangeCol = true, stackPlus = false, sortCards = false, gameEnded = false;
    private static Image back;
    private static final Color none = new Color(255, 255, 255, 255);

    public static void main(String[] args) {
        window = new JFrame("Uno");
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
        c.add(stats(), "stats");
        gameSettings = gameSettings();
        c.add(gameSettings, "settings");
        window.setContentPane(c);

        window.setLocation(0,0);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setIconImage(back);
        window.setVisible(true);
    }

    public void setBotGame() {
        //Adds single player screen to container to play against bot
        c.add(botPlayingScreen(), "player");
        c.add(playerBotWins(), "playerWin");
        c.add(botWins(), "botWin");
        c.add(tieGame(), "tieGame");
        statCounts[0]++;
        statCounts[1]++;
        gameEnded = false;
    }

    public void setPlayerGame() {
        //Adds player screens to the container to play against others
        c.add(blank, "blank");
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            c.add(playerPlayingScreen(i), "player" + i);
            c.add(playerNWins(i), "player" + i + "Win");
            statCounts[11 + i]++;
        }
        c.add(tieGame(), "tieGame");
        statCounts[0]++;
        statCounts[2]++;
        gameEnded = false;
    }

    public void resetC() {
        //Resets container
        c.removeAll();
        c.add(menu());
        c.add(stats(), "stats");
        c.add(gameSettings, "settings");
        botCards.clear();
        playerCardsLeft.clear();
        drawCardsLeft.clear();
        placePileCard.clear();
        pCards.clear();
    }

    public static JPanel menu() {
        //Creates menu screen
        JPanel menu = new JPanel();
        menu.setLayout(new BorderLayout());
        //Choose if you play against bot or real people
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(5, 1));
        JButton bot = new JButton("Bot");
        bot.addActionListener(listener);
        bot.setFont(new Font(ARIAL, Font.PLAIN, 25));
        buttons.add(bot);
        JPanel player = new JPanel();
        player.setLayout(new GridLayout(1, 2));
        JButton players = new JButton("Players: ");
        players.addActionListener(listener);
        players.setFont(new Font(ARIAL, Font.PLAIN, 25));
        player.add(players);
        JSlider playerCount = new JSlider(2, 4, 2);
        playerCount.addChangeListener(listener);
        playerCount.setMajorTickSpacing(1);
        playerCount.setPaintTicks(true);
        playerCount.setPaintLabels(true);
        playerCount.setBackground(none);
        player.add(playerCount);
        player.setBackground(none);
        buttons.add(player);
        JTextArea info = new JTextArea("The game takes a bit to start. Pressing the button again makes you draw a card right away. So don't do it.");
        info.setEditable(false);
        info.setFont(new Font(ARIAL, Font.ITALIC, 15));
        info.setForeground(Color.RED);
        JPanel infoPan = new JPanel();
        infoPan.add(info, BorderLayout.CENTER);
        infoPan.setBackground(none);
        buttons.add(infoPan);
        JButton stats = new JButton("Stats");
        stats.addActionListener(listener);
        stats.setFont(new Font(ARIAL, Font.PLAIN, 25));
        buttons.add(stats);
        JButton settings = new JButton("Settings");
        settings.addActionListener(listener);
        settings.setFont(new Font(ARIAL, Font.PLAIN, 25));
        buttons.add(settings);
        buttons.setBackground(none);
        menu.add(buttons, BorderLayout.SOUTH);
        //Welcome text at top of screen
        JTextArea welcome = new JTextArea("Welcome to the game of Uno.\n" + "Each player starts with 7 cards and first with 0 left wins.\n" + "The same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\n" + "If you can't go, you will have to draw a card.");
        welcome.setFont(new Font(ARIAL, Font.PLAIN, 25));
        welcome.setEditable(false);
        JPanel welcomePan = new JPanel();
        welcomePan.add(welcome, BorderLayout.CENTER);
        welcomePan.setBackground(none);
        menu.add(welcomePan, BorderLayout.NORTH);
        //Image of back of Uno card at center
        targetWidth = dims.getWidth() / 5;
        targetHeight = targetWidth * 143 / 100;
        JLabel picLabel = new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)));
        picLabel.setBackground(none);
        menu.add(picLabel, BorderLayout.CENTER);
        menu.setBackground(none);

        return menu;
    }

    public JPanel botPlayingScreen() {
        //Creates screen for a player to play against the bot
        JPanel botPlayingScreen = new JPanel();
        botPlayingScreen.setLayout(new BorderLayout());
        //Top of the screen
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1, 2));
        top.setBackground(none);
        //Left top (cards left and go to menu)
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(3, 1));
        JButton goMenu = new JButton("Menu");
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        goMenu.addActionListener(listener);
        left.add(goMenu);
        pCardsLeft = new JLabel("\nCards left:  Bot's cards: " + listener.pCardsLeft(1) + " - Player's cards: " + listener.pCardsLeft(0));
        pCardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerCardsLeft.add(pCardsLeft);
        JPanel centPlayerCardsLeft = new JPanel();
        centPlayerCardsLeft.add(playerCardsLeft.get(0), BorderLayout.CENTER);
        centPlayerCardsLeft.setBackground(none);
        left.add(centPlayerCardsLeft);
        botsPlay = new JLabel(tab + tab + "Bot's play: " + listener.botsPlay());
        botsPlay.setFont(new Font(ARIAL, Font.PLAIN, 20));
        left.add("botsplay", botsPlay);
        left.setBackground(none);
        top.add(left);
        //Top right (cards left in draw pile w/ pic)
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(3, 1));
        JButton printHands = new JButton("Print Hands");
        printHands.addActionListener(listener);
        printHands.setFont(new Font(ARIAL, Font.PLAIN, 30));
        right.add(printHands);
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        right.add(new JLabel(new ImageIcon(back.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH))));
        cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft());
        cardsLeft.setFont(new Font(ARIAL, Font.PLAIN, 20));
        drawCardsLeft.add(cardsLeft);
        JPanel centDrawCardsLeft = new JPanel();
        centDrawCardsLeft.add(drawCardsLeft.get(0), BorderLayout.CENTER);
        centDrawCardsLeft.setBackground(none);
        right.add(centDrawCardsLeft);
        right.setBackground(none);
        top.add(right);
        botPlayingScreen.add(top, BorderLayout.NORTH);
        //Center (card on place pile)
        targetWidth = dims.getWidth() / 15;
        targetHeight = targetWidth * 143 / 100;
        Image img = listener.getPlacePile().getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        placePile = new JLabel(new ImageIcon(img));
        placePileCard.add(placePile);
        botPlayingScreen.add(placePileCard.get(0), BorderLayout.CENTER);
        //Bottom (cards on hand and draw)
        JPanel bottomCards = new JPanel();
        bottomCards.setLayout(new GridLayout(2, 1));
        JButton draw = new JButton("Draw");
        draw.setFont(new Font(ARIAL, Font.BOLD, 40));
        draw.addActionListener(listener);
        pCards.add(initialSet(0));
        bottomCards.add(pCards.get(0));
        bottomCards.add(draw);
        bottomCards.setBackground(none);
        botCards.add(bottomCards);
        botPlayingScreen.add(botCards.get(0), BorderLayout.SOUTH);
        botPlayingScreen.setBackground(none);

        return botPlayingScreen;
    }

    public JPanel playerPlayingScreen(int player) {
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
        cardsLeft = new JLabel("Cards in drawpile: " + listener.getCardsLeft());
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
        Image img = listener.getPlacePile().getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
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

    public void removeCard(int player) {
        int toRemove = listener.getPlayedCard();
        if (toRemove != -1) {
            pCards.get(player).remove(toRemove);
            listener.removeCard();
        }
    }

    public void updatePlayerCards(int player) {
        JPanel newCards = pCards.get(player);
        Hand playerHand = listener.getPlayerHand(player);
        int div = 20;
        if (playerHand.length() > 13) {
            bigger13 = true;
            div += (playerHand.length() / 7) * playerHand.length();
        }
        targetWidth = dims.getWidth() / div;
        targetHeight = targetWidth * 143 / 100;
        //TODO Optimize size changeing
        if (bigger13) {
            newCards = new JPanel();
            for (int i = 0; i < playerHand.length(); i++) {
                JButton card = new JButton(playerHand.getCard(i).toString());
                Image img = playerHand.getCard(i).getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                card.setIcon(new ImageIcon(img));
                card.addActionListener(listener);
                card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
                card.setSize(targetWidth, targetHeight);
                newCards.add(card);
            }
        } else {
            int amtNewCards = listener.newCardsAdded();
            if (amtNewCards > 0) {
                for (int i = amtNewCards; i > 0; i--) {
                    JButton card = new JButton(playerHand.getCard(playerHand.length() - i).toString());
                    Image img = playerHand.getCard(playerHand.length() - i).getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
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

    public void updateCardElements() {
        setPanelDims(window.getWidth(), window.getHeight());
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
        placePile.setIcon(new ImageIcon(listener.getPlacePile().getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)));
        repaint();
    }

    public void updateCards(int player) {
        setPanelDims(window.getWidth(), window.getHeight());
        JPanel toUpdate = botCards.get(player);
        toUpdate.setVisible(false);
        toUpdate.removeAll();
        toUpdate.setLayout(new GridLayout(2, 1));
        updatePlayerCards(player);
        toUpdate.setBackground(none);
        toUpdate.add(pCards.get(player));
        if (listener.getCardsLeft() != 0) {
            JButton draw = new JButton("Draw");
            draw.setFont(new Font(ARIAL, Font.BOLD, 40));
            draw.addActionListener(listener);
            toUpdate.add(draw);
        }
        toUpdate.setVisible(true);
        repaint();
    }

    public static JPanel stats() {
        JPanel stats = new JPanel();
        stats.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        stats.add(goMenu, BorderLayout.NORTH);
        JPanel totStats = new JPanel();
        totStats.setLayout(new GridLayout(2, 1));
        //Total games played
        JPanel centTotGames = new JPanel();
        JLabel totGames = new JLabel("Total games played: " + statCounts[0]);
        totGames.setFont(new Font(ARIAL, Font.PLAIN, 40));
        centTotGames.add(totGames, BorderLayout.CENTER);
        totStats.add(centTotGames, BorderLayout.NORTH);
        //Stats for individual game categories
        JPanel indivStats = new JPanel();
        indivStats.setLayout(new GridLayout(1, 2));
        //Stats for games against the bot
        JPanel botStats = new JPanel();
        botStats.setLayout(new GridLayout(4, 1));
        JLabel totBotGames = new JLabel("Total games played against the bot: " + statCounts[1]);
        totBotGames.setFont(new Font(ARIAL, Font.PLAIN, 30));
        botStats.add(totBotGames);
        String cent = " times which is ";
        JLabel playerWins = new JLabel("The player has won " + statCounts[3] + cent + (statCounts[3] * 100 / Math.max(statCounts[1], 1)) + "% (" + statCounts[3] + "/" + statCounts[1] + ") of all games against the bot.");
        playerWins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        botStats.add(playerWins);
        JLabel botWins = new JLabel("The bot has won " + statCounts[4] + cent + (statCounts[4] * 100 / Math.max(statCounts[1], 1)) + "% (" + statCounts[4] + "/" + statCounts[1] + ") of all games against it.");
        botWins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        botStats.add(botWins);
        JLabel botTie = new JLabel("The game was tied " + statCounts[5] + cent + (statCounts[5] * 100 / Math.max(statCounts[1], 1)) + "% (" + statCounts[5] + "/" + statCounts[1] + ") of all games against the bot.");
        botTie.setFont(new Font(ARIAL, Font.PLAIN, 20));
        botStats.add(botTie);
        indivStats.add(botStats);
        //Stats for games against other players
        JPanel playerStats = new JPanel();
        playerStats.setLayout(new GridLayout(6, 1));
        JLabel totPlayerGames = new JLabel("Total games played against other players: " + statCounts[2]);
        totPlayerGames.setFont(new Font(ARIAL, Font.PLAIN, 30));
        playerStats.add(totPlayerGames);
        JLabel player1Wins = new JLabel("Player 1 has won " + statCounts[6] + cent + (statCounts[6] * 100 / Math.max(statCounts[11], 1)) + "% (" + statCounts[6] + "/" + statCounts[11] + ") of all games against others.");
        player1Wins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerStats.add(player1Wins);
        JLabel player2Wins = new JLabel("Player 2 has won " + statCounts[7] + cent + (statCounts[7] * 100 / Math.max(statCounts[12], 1)) + "% (" + statCounts[7] + "/" + statCounts[12] + ") of all games against others.");
        player2Wins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerStats.add(player2Wins);
        JLabel player3Wins = new JLabel("Player 3 has won " + statCounts[8] + cent + (statCounts[8] * 100 / Math.max(statCounts[13], 1)) + "% (" + statCounts[8] + "/" + statCounts[13] + ") of all games against others.");
        player3Wins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerStats.add(player3Wins);
        JLabel player4Wins = new JLabel("Player 4 has won " + statCounts[9] + cent + (statCounts[9] * 100 / Math.max(statCounts[14], 1)) + "% (" + statCounts[9] + "/" + statCounts[14] + ") of all games against others.");
        player4Wins.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerStats.add(player4Wins);
        JLabel playerTie = new JLabel("The game was tied " + statCounts[10] + cent + (statCounts[10] * 100 / Math.max(statCounts[2], 1)) + "% (" + statCounts[10] + "/" + statCounts[2] + ") of all games against others.");
        playerTie.setFont(new Font(ARIAL, Font.PLAIN, 20));
        playerStats.add(playerTie);
        indivStats.add(playerStats);
        indivStats.setBackground(none);
        totStats.add(indivStats, BorderLayout.SOUTH);
        stats.add(totStats, BorderLayout.CENTER);

        return stats;
    }

    public static JPanel gameSettings() {
        JPanel settings = new JPanel();
        settings.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        settings.add(goMenu, BorderLayout.NORTH);
        JPanel clickSets = new JPanel();
        clickSets.setLayout(new GridLayout(3, 1));
        JCheckBox sort = new JCheckBox("Sort cards on hand by color (takes longer to load)");
        sort.addActionListener(listener);
        sort.addChangeListener(listener);
        clickSets.add(sort);
        JCheckBox stackChangeCol = new JCheckBox("Can't stack Change Color cards on top of each other");
        stackChangeCol.addActionListener(listener);
        stackChangeCol.addChangeListener(listener);
        clickSets.add(stackChangeCol);
        JCheckBox stackPlus = new JCheckBox("Can stack Plus 2 cards on top of each other");
        stackPlus.addActionListener(listener);
        stackPlus.addChangeListener(listener);
        clickSets.add(stackPlus);
        settings.add(clickSets);

        return settings;
    }

    public static JPanel playerBotWins() {
        JPanel playerBotWins = new JPanel();
        playerBotWins.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        playerBotWins.add(goMenu, BorderLayout.NORTH);
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
        playerNWins.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        playerNWins.add(goMenu, BorderLayout.NORTH);
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
        botWins.setLayout(new BorderLayout());
        JButton goMenu = new JButton("Menu");
        goMenu.addActionListener(listener);
        goMenu.setFont(new Font(ARIAL, Font.PLAIN, 30));
        botWins.add(goMenu, BorderLayout.NORTH);
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
        botWins.add(new JLabel(new ImageIcon(robot)), BorderLayout.SOUTH);
        botWins.setBackground(none);

        return botWins;
    }

    public static JPanel tieGame() {
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

    private JPanel initialSet(int player) {
        JPanel initialCards = new JPanel();
        Hand playerHand = listener.getPlayerHand(player);
        targetWidth = dims.getWidth() / 20;
        targetHeight = targetWidth * 143 / 100;
        for (int i = 0; i < playerHand.length(); i++) {
            JButton card = new JButton(playerHand.getCard(i).toString());
            Image img = playerHand.getCard(i).getImage(1).getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            card.setIcon(new ImageIcon(img));
            card.addActionListener(listener);
            card.setFont(new Font(card.getFont().toString(), Font.PLAIN, 0));
            card.setSize(targetWidth, targetHeight);
            initialCards.add(card);
        }

        return initialCards;
    }

    public void goToMenu() {
        screen.first(c);
    }

    public void goToStats() {
        screen.show(c, "stats");
    }

    public void goToSettings() {
        screen.show(c, "settings");
    }

    public void showScreen(String toShow) {
        screen.show(c, toShow);
    }

    public void playerScreen(String player) {
        screen.show(c, player);
    }

    public void printAllHands() {
        for (int i = 0; i < listener.getPlayerCount(); i++) {
            System.out.println("Player " + (i + 1) + ":\n" + listener.getPlayerHand(i).toString());
        }
        System.out.println();
    }

    public void endResult(int winner) {
        statCounts[winner]++;
    }

    public void setGameEnded() { //TODO Maybe also add game started to keep track of aborted games
        gameEnded = true;
    }

    public void setStackChangeCol(boolean toSetTo) {
        stackChangeCol = toSetTo;
    }

    public void setStackPlus(boolean toSetTo) {
        stackPlus = toSetTo;
    }

    public void setSort(boolean toSetTo) {
        sortCards = toSetTo;
    }

    public void setPanelDims(int width, int height) {
        dims.setWidth(width);
        dims.setHeight(height);
    }
}
