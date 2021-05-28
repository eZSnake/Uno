import java.awt.*;

public class Switch extends Card {
    private boolean hasSwitched;

    public Switch(String col, Image img) {
        super(col, 1, -1, img);
        hasSwitched = false;
    }

    public String toString() {
        return getColor() + " Switch";
    }

    public boolean getHasSwitched() {
        return hasSwitched;
    }

    public void setHasSwitched(boolean hasSwitched) {
        this.hasSwitched = hasSwitched;
    }
}
