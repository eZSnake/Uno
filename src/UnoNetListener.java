import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoNetListener implements ActionListener {
    private UnoPlayerClient client;

    public UnoNetListener(UnoPlayerClient client) {
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Player 1":
                client.setPlayer(1);
                client.repaint();
                break;
            case "Player 2":
                client.setPlayer(2);
                client.repaint();
                break;
            case "Player 3":
                client.setPlayer(3);
                client.repaint();
                break;
            case "Player 4":
                client.setPlayer(4);
                client.repaint();
                break;
            case "Continue":
                client.nextScreen();
                client.repaint();
                break;
        }
    }
}
