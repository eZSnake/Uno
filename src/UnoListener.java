import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class UnoListener implements ActionListener, ChangeListener, ComponentListener {
    private UnoPanel panel;
    private int playerCount = 2;
    private UnoGraphicsGame game = new UnoGraphicsGame(2); //TODO remove everything after = for full implementation?
    private boolean botGame = false;

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setWinnerScreen();
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
                //TODO Implement switching screens for players -> DONE
                //TODO Player 1 screen not displaying info properly
                //TODO Currently crashes after drawing card?
                botGame = false;
                game = new UnoGraphicsGame(playerCount);
                panel.setPlayerGame();
                panel.playerScreen("player0");
                panel.repaint();
                break;
            case "Draw":
                //TODO Card overflow when having over 9 cards on hand
                if (botGame) {
                    game.draw(0);
                } else {
                    game.draw();
                }
                panel.updateCardElements();
                if (botGame && game.getPlayer() == 1) {
                    game.botPlayCard();
                } else if (!botGame) {
                    panel.playerScreen("player" + game.getPlayer());
                    //TODO Crashes everything (can't click buttons after)
//                    JOptionPane.showMessageDialog(panel, "Player " + (game.getPlayer() + 1) + "'s turn. Click OK to continue.");
                }
                updateWholeScreen();
                break;
            case "Menu":
                panel.resetC();
                panel.goToMenu();
                panel.repaint();
                break;
            default:
                updateWholeScreen();
                Card toPlay = game.stringToCard(button);
                if (!game.canPlayCard(toPlay)) {
                    //TODO Don't make it crash everything (can't click buttons after)
                    JOptionPane.showMessageDialog(panel, "That card can't be played.");
                    break;
                }
                game.playCard(toPlay);
                if (toPlay.getId() == 4) {
                    Object[] options = {"Blue", "Green", "Red", "Yellow"};
                    int col = JOptionPane.showOptionDialog(panel, "What color would you like? Please select below.", "Choose color", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
                    if (col == -1) {
                        JOptionPane.showMessageDialog(panel, "A random color will now be chosen for you as you have failed to follow instructions");
                        col = (int) (Math.random() * 4);
                    }
                    game.doSpecialMove(options[col].toString());
                } else {
                    game.doSpecialMove(null);
                }
                panel.updateCardElements();
                game.nextPlayer();
                if (botGame && game.getPlayer() == 1) {
                    System.out.println("Bots turn");
                    game.botPlayCard();
                } else if (!botGame) {
                    panel.playerScreen("player" + game.getPlayer());
//                    JOptionPane.showMessageDialog(panel, "Player " + (game.getPlayer() + 1) + "'s turn. Click OK to continue.");
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

    public Deck getDeck() {
        return game.getDeck();
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

    private void setWinnerScreen() {
        if (botGame) {
            switch (game.determineWinner()) {
                case 0:
                    panel.showScreen("playerWin");
                    break;
                case 1:
                    panel.showScreen("botWin");
                    break;
                case 4:
                    panel.showScreen("tieGame");
                    break;
            }
        } else {
            switch (game.determineWinner()) {
                case 0:
                    panel.showScreen("player0Win");
                    break;
                case 1:
                    panel.showScreen("player1Win");
                    break;
                case 2:
                    panel.showScreen("player2Win");
                    break;
                case 3:
                    panel.showScreen("player3Win");
                    break;
                case 4:
                    panel.showScreen("tieGame");
                    break;
            }
        }
        panel.removeAll();
        panel.repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Component c = (Component) e.getSource();
        panel.setPanelDims(c.getWidth(), c.getHeight());
        panel.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    private void updateWholeScreen() {
        panel.updateCardElements();
        panel.updateCards(game.getPlayer());
        setWinnerScreen();
    }
}
