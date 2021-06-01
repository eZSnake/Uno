import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class UnoListener implements ActionListener, ChangeListener {
    private final UnoPanel panel;
    private int playerCount = 2;
    private UnoGraphicsGame game = new UnoGraphicsGame(2); //TODO remove everything after = for full implementation?
    private boolean botGame = false;
    private Card toPlay;

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String button = e.getActionCommand();
        switch (button) {
            case "Bot":
                botGame = true;
                game = new UnoGraphicsGame(2);
                panel.setBotGame();
                panel.nextScreen();
                panel.repaint();
                break;
            case "Players: ":
                botGame = false;
                game = new UnoGraphicsGame(playerCount);
                panel.setPlayerGame();
                panel.playerScreen("player0");
                panel.repaint();
                break;
            case "Draw":
                if (botGame) {
                    game.resetBotsPlay();
                    game.draw(0);
                } else {
                    game.draw();
                }
                if (setWinnerScreen()) {
                    break;
                }
                if (botGame && game.getPlayer() == 1) {
                    game.botPlayCard();
                } else if (!botGame) {
                    panel.showScreen("blank");
                    JOptionPane.showMessageDialog(panel, "Player " + (game.getPlayer() + 1) + "'s turn. Click OK to continue.");
                    panel.playerScreen("player" + game.getPlayer());
                }
                updateWholeScreen();
                break;
            case "Menu":
                panel.resetC();
                panel.goToMenu();
                panel.repaint();
                break;
            default:
                toPlay = game.stringToCard(button);
                if (!game.canPlayCard(toPlay)) {
                    JOptionPane.showMessageDialog(panel, "That card can't be played.");
                    panel.updateCards(game.getPlayer());
                    break;
                }
                if (!(toPlay.toString().contains("+") || toPlay.toString().contains("Skip"))) {
                    game.resetBotsPlay();
                }
                game.playCard(toPlay);
                if (toPlay.getId() == 4) {
                    Object[] options = {"Blue", "Green", "Red", "Yellow"};
                    int col = JOptionPane.showOptionDialog(panel, "What color would you like? Please select below.", "Choose color", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
                    if (col == -1) {
                        JOptionPane.showMessageDialog(panel, "A random color will now be chosen for you as you have failed to follow instructions");
                        col = (int)(Math.random() * 4);
                    }
                    game.doSpecialMove(options[col].toString());
                } else {
                    game.doSpecialMove(null);
                }
                if (setWinnerScreen()) {
                    break;
                }
                int prevPlayer = game.getPlayer();
                game.nextPlayer();
                if (botGame && game.getPlayer() == 1) {
                    game.botPlayCard();
                } else if (!botGame) {
                    if (game.getPlayer() != prevPlayer) {
                        panel.showScreen("blank");
                        JOptionPane.showMessageDialog(panel, "Player " + (game.getPlayer() + 1) + "'s turn. Click OK to continue.");
                    }
                    panel.playerScreen("player" + game.getPlayer());
                }
                updateWholeScreen();
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            playerCount = source.getValue();
        }
    }

    public int pCardsLeft(int plyr) {
        if (game == null) {
            return 0;
        }
        return game.getPCardsLeft(plyr);
    }

    public int getCardsLeft() {
        return game.getCardsLeft();
    }

    public Hand getPlayerHand(int plyr) {
        return game.getPlayerHand(plyr);
    }

    public int getPlayer() {
        return game.getPlayer();
    }

    public Card getPlacePile() {
        return game.getPlacePile();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public boolean isBotGame() {
        return botGame;
    }

    public String botsPlay() {
        return game.getBotsPlay();
    }

    public int getPlayedCard() {
        return game.getIndexOfCard(toPlay);
    }

    public int newCardsAdded() {
        return game.newCardsAdded();
    }

    private boolean setWinnerScreen() {
        boolean switched = false;
        if (botGame) {
            switch (game.determineWinner()) {
                case 0:
                    panel.showScreen("playerWin");
                    switched = true;
                    break;
                case 1:
                    panel.showScreen("botWin");
                    switched = true;
                    break;
                case 4:
                    panel.showScreen("tieGame");
                    switched = true;
                    break;
            }
        } else {
            switch (game.determineWinner()) {
                case 0:
                    panel.showScreen("player0Win");
                    switched = true;
                    break;
                case 1:
                    panel.showScreen("player1Win");
                    switched = true;
                    break;
                case 2:
                    panel.showScreen("player2Win");
                    switched = true;
                    break;
                case 3:
                    panel.showScreen("player3Win");
                    switched = true;
                    break;
                case 4:
                    panel.showScreen("tieGame");
                    switched = true;
                    break;
            }
        }
        panel.revalidate();
        panel.repaint();
        return switched;
    }

    private void updateWholeScreen() {
        if (!setWinnerScreen()) {
            panel.updateCardElements();
            panel.updateCards(game.getPlayer());
        }
    }
}
