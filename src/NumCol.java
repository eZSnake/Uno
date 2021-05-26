import java.awt.*;

public class NumCol extends Card {
    public NumCol(String col, int num, Image img) {
        super(col, 0, num, img);
    }

    public String toString() {
        return getColor() + " " + getNum();
    }
}
