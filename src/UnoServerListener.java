import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class UnoServerListener implements ActionListener, ChangeListener {
    private int playerCount = 2;
    private UnoPlayerServer server;

    UnoServerListener(UnoPlayerServer server) {
        this.server = server;
    }

    public UnoServerListener() {

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            playerCount = source.getValue();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Start":
                server.start();
                break;
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
