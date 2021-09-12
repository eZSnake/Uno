import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class ServerListener implements ActionListener, ChangeListener {
    private int playerCount = 2;
    private UnoServer server;

    public ServerListener(UnoServer server) {
        this.server = server;
//        server.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case "Start" -> {
                System.out.println("Starting");
                server.start();
                server.repaint();
            }
            case "Menu" -> {
                server.goMenu();
                server.repaint();
            }
            default -> {
                System.out.println("Reached base case");
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            playerCount = source.getValue();
            System.out.println("Number of players: " + source.getValue());
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
