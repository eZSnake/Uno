import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoListener implements ActionListener, ChangeListener {
    private UnoPanel panel;
    private int playerCount;
    private Game game = new BotUnoGraphicsGame(); //TODO remove everything after = for full implementation

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
            panel.repaint();
        } else if (a.equals("Menu")) {
            panel.goToMenu();
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
}
