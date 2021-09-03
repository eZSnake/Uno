import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoNetListener implements ActionListener {
    private UnoPlayerClient client;
    private int player;

    public UnoNetListener(UnoPlayerClient client) {
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String button = e.getActionCommand();
        switch (button) {
            case "Player 1" -> {
                client.setPlayer(1);
                player = 1;
                client.repaint();
            }
            case "Player 2" -> {
                client.setPlayer(2);
                player = 2;
                client.repaint();
            }
            case "Player 3" -> {
                client.setPlayer(3);
                player = 3;
                client.repaint();
            }
            case "Player 4" -> {
                client.setPlayer(4);
                player = 4;
                client.repaint();
            }
            case "Continue" -> {
                if (player != 0) {
                    client.nextScreen();
                    client.startConnection();
                    client.repaint();
                } else {
                    JOptionPane.showMessageDialog(client, "You must select a valid player.");
                }
            }
            case "Menu" -> {
                client.goMenu();
                client.repaint();
            }
            case "Draw" -> {
                if (player == client.getCurrPlayer()) {
                    client.draw();
                    client.updateWholeScreen();
                } else {
                    JOptionPane.showMessageDialog(client, "It is not your turn.");
                }
            }
            default -> {
                if (player != client.getCurrPlayer()) {
                    JOptionPane.showMessageDialog(client, "It is not your turn.");
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
                client.updateWholeScreen();
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
        //Checks if inputted card can be played
        if (toPlay == null) {
            return false;
        }
        Card placePile = client.getPlacePile();
        return toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum();
    }
}
