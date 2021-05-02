import java.awt.image.BufferedImage;

public class NumCol extends Card {
    public NumCol(String col, int num, BufferedImage img) {
        super(col, 0, num, img);
    }

    public String toString() {
        return getColor() + " " + getNum();
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, String col) {
    }
}
