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
    private BotUnoGraphicsGame game = new BotUnoGraphicsGame(); //TODO remove everything after = for full implementation

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setWinnerScreen();
        String a = e.getActionCommand();
        System.out.println(a);
        if (a.equals("Bot")) {
//            game = new BotUnoGraphicsGame();
            panel.nextScreen();
            panel.repaint();
//            game.playRounds();
        } else if (a.equals("Players: ")) {
            UnoGame myGame = new UnoGame(playerCount);
            myGame.playRounds(0);
            panel.repaint();
        } else if (a.equals("Draw")) {
            game.draw(0);
            System.out.println("Drawing card");
            System.out.println(getPlayerHand().length());
            panel.updateCardElements();
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
//            panel.getComponent(1).list();
        } else if (a.equals("Menu")) {
            panel.goToMenu();
            panel.repaint();
        } else {
            Card toPlay = game.stringToCard(a);
            if (game.canPlayCard(toPlay)) {
                if (toPlay.getId() == 4) {
                    Object[] options = {"Blue", "Green", "Red", "Yellow"};
                    int col = JOptionPane.showOptionDialog(panel, "What color would you like? Please select below.", "Choose color", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
                    System.out.println(col);
                    if (col == -1) {
                        JOptionPane.showMessageDialog(panel, "A random color will now be chosen for you as you have failed to follow instructions");
                        col = 1 + (int)(Math.random() * 4);
                    }
                }
                game.playCard(toPlay);
            } else {
                JOptionPane.showMessageDialog(panel, "That card can't be played.");
            }
            panel.updateCardElements();
            panel.repaint();
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

    public Hand getPlayerHand() {
        return game.getPlayerHand();
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
                panel.nextScreen();
                panel.repaint();
            case 1:
                panel.nextScreen();
                panel.nextScreen();
                panel.repaint();
            case 2:
                panel.nextScreen();
                panel.nextScreen();
                panel.nextScreen();
                panel.repaint();
        }
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
}
