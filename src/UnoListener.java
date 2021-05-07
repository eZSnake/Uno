import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoListener implements ActionListener, ChangeListener {
    private UnoPanel panel;
    private int playerCount;
    private BotUnoGraphics botGame = new BotUnoGraphics(); //TODO remove everything after = for full implementation

    public UnoListener(UnoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String a = e.getActionCommand();
        if (a.equals("Bot")) {
//            botGame = new BotUnoGraphics();
            botGame.playRounds();
            panel.nextScreen();
        } else if (a.equals("Players: ")) {
            UnoGame myGame = new UnoGame(playerCount);
            myGame.playRounds(0);
        } else if (a.equals("Draw")) {
            botGame.draw(0);
            System.out.println("Drawing card");
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
        return botGame.getPCardsLeft(plyr);
    }

    public int getCardsLeft() {
        return botGame.getCardsLeft();
    }

    public Hand getPlayerHand() {
        return botGame.getPlayerHand();
    }
}
