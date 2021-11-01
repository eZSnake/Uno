import java.awt.*;

public class Skip extends Card {
    boolean hasSkipped;

    public Skip(String col, String imgLoc) {
        super(col, 2, -2, imgLoc);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " Skip";
    }

    public boolean getHasSkipped() {
        return hasSkipped;
    }

    public void setHasSkipped(boolean hasSkipped) {
        this.hasSkipped = hasSkipped;
    }
}
