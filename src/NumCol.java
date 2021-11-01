import java.awt.*;

public class NumCol extends Card {
    public NumCol(String col, int num, String imgLoc) {
        super(col, 0, num, imgLoc);
    }

    public String toString() {
        return getColor() + " " + getNum();
    }
}
