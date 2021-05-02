import java.awt.image.BufferedImage;

public class Switch extends Card {
    private boolean hasSwitched;

    public Switch(String col, BufferedImage img) {
        super(col, 1, -1, img);
        hasSwitched = false;
    }

    public String toString() {
        return getColor() + " Switch";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, String col) {
    }

    public boolean getHasSwitched() {
        return hasSwitched;
    }

    public void setHasSwitched(boolean hasSwitched) {
        this.hasSwitched = hasSwitched;
    }
}
