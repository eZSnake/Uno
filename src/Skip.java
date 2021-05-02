import java.awt.image.BufferedImage;

public class Skip extends Card {
    boolean hasSkipped;

    public Skip(String col, BufferedImage img) {
        super(col, 2, -2, img);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " Skip";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, String col) { }

    public boolean getHasSkipped() {
        return hasSkipped;
    }

    public void setHasSkipped(boolean hasSkipped) {
        this.hasSkipped = hasSkipped;
    }
}
