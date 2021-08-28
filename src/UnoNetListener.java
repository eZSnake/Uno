import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoNetListener implements ActionListener {
    private UnoPlayerClient client;
    private int player;

    public UnoNetListener(UnoPlayerClient client) {
        this.client = client;
        this.player = client.getPlayer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String button = e.getActionCommand();
        switch (button) {
            case "Player 1" -> {
                client.setPlayer(1);
                client.repaint();
            }
            case "Player 2" -> {
                client.setPlayer(2);
                client.repaint();
            }
            case "Player 3" -> {
                client.setPlayer(3);
                client.repaint();
            }
            case "Player 4" -> {
                client.setPlayer(4);
                client.repaint();
            }
            case "Continue" -> {
                client.nextScreen();
                client.startConnection();
                client.repaint();
            }
            case "Menu" -> {
                client.goMenu();
                client.repaint();
            }
            default -> {
                if (player != client.getCurrPlayer()) {
                    JOptionPane.showMessageDialog(client, "It is not your turn.");
                    client.updateCards();
                    break;
                }
                Card toPlay = stringToCard(button);
                if (!canPlayCard(toPlay)) {
                    JOptionPane.showMessageDialog(client, "That card can't be played.");
                    client.updateCards();
                    break;
                }
                if (toPlay.getId() == 4) {
                    Object[] options = {"Blue", "Green", "Red", "Yellow"};
                    int col = JOptionPane.showOptionDialog(client, "What color would you like? Please select below.", "Choose color", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
                    if (col == -1) {
                        JOptionPane.showMessageDialog(client, "A random color will now be chosen for you as you have failed to follow instructions");
                        col = (int)(Math.random() * 4);
                    }
                    client.playCard(toPlay, options[col].toString());
                } else {
                    client.playCard(toPlay, null);
                }
                client.removeCard(toPlay);
//                game.nextPlayer();
                updateWholeScreen();
                break;
            }
        }
    }

    private Card stringToCard(String conv) {
        //Converts an inputted string to a card on the hand of the current player
        try {
            Hand toCheck = client.getHand();
            for (int i = 0; i < toCheck.length(); i++) {
                if (toCheck.getCard(i).toString().equals(conv)) {
                    return toCheck.getCard(i);
                }
            }
            throw new Exception("FATAL - in stringToCard - card not member of hand");
        } catch (Exception ignored) {}
        return null;
    }

    private boolean canPlayCard(Card toPlay) {
        if (toPlay == null) {
            return false;
        }
        Card placePile = client.getPlacePile();
        return toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum();
    }

    private boolean setWinnerScreen() {

    }
}
