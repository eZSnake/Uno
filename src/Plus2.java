import java.awt.*;

public class Plus2 extends Card {
    private boolean hasSkipped;

    public Plus2(String col, Image img) {
        super(col, 3, -3, img);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " +2";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, String col) {
        if (currPlayer < hands.length-1) {
            currPlayer++;
        } else {
            currPlayer = 0;
        }
        for (int i = 0; i < 2; i++) {
            hands[currPlayer].addCard(deck.deal());
        }
    }

    public boolean getHasSkipped() {
        return hasSkipped;
    }

    public void setHasSkipped(boolean hasSkipped) {
        this.hasSkipped = hasSkipped;
    }
}
