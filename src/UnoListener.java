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
    private int playerCount;
    private BotUnoGraphicsGame game = new BotUnoGraphicsGame(this); //TODO remove everything after = for full implementation

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setWinnerScreen();
        String a = e.getActionCommand();
        switch (a) {
            case "Bot":
//            game = new BotUnoGraphicsGame();
                panel.nextScreen();
                panel.repaint();
//                game.playRounds();
                break;
            case "Players: ":
                UnoGame myGame = new UnoGame(playerCount);
                myGame.playRounds(0);
                panel.repaint();
                break;
            case "Draw":
                //TODO Card overflow when having over 9 cards on hand
                game.draw(0);
                panel.updateCardElements();
                panel.repaint();
                if (game.getPlayer() == 1) {
                    System.out.println("Bots turn");
                    game.botPlayCard();
                }
                updateWholeScreen();
                break;
            case "Menu":
                panel.goToMenu();
                panel.repaint();
                break;
            default:
                System.out.println("Player played");
                Card toPlay = game.stringToCard(a);
                if (game.canPlayCard(toPlay)) {
                    game.playCard(toPlay);
                    if (toPlay.getId() == 4) {
                        Object[] options = {"Blue", "Green", "Red", "Yellow"};
                        int col = JOptionPane.showOptionDialog(panel, "What color would you like? Please select below.", "Choose color", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
                        if (col == -1) {
                            JOptionPane.showMessageDialog(panel, "A random color will now be chosen for you as you have failed to follow instructions");
                            col = (int) (Math.random() * 4);
                        }
                        game.doSpecialMove(options[col].toString());
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "That card can't be played.");
                }
                panel.updateCardElements();
                panel.repaint();
                if (game.getPlayer() == 1) {
                    System.out.println("Bots turn");
                    game.botPlayCard();
                }
                updateWholeScreen();
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
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

    private void setWinnerScreen() {
        switch (game.determineWinner()) {
            case 0:
                panel.showScreen("playerWin");
                break;
            case 1:
                panel.showScreen("botWin");
                break;
            case 2:
                panel.showScreen("tieGame");
                break;
        }
        panel.removeAll();
        panel.repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Component c = (Component)e.getSource();
        panel.setPanelDims(c.getWidth(), c.getHeight());
        panel.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }

    private void updateWholeScreen() {
        panel.updateCardElements();
        panel.updateCards();
        setWinnerScreen();
        panel.repaint();
    }
}
