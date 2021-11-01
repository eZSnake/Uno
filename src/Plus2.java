import java.awt.*;

public class Plus2 extends Card {
    private boolean hasSkipped;

    public Plus2(String col, String imgLoc) {
        super(col, 3, -3, imgLoc);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " +2";
    }

    @Override
    public void specialMove(Deck deck, Hand[] hands, int currPlayer, boolean rev, String col) {
        if (rev) {
            currPlayer = Math.floorMod(currPlayer - 1, hands.length);
        } else {
            currPlayer = Math.floorMod(currPlayer + 1, hands.length);
        }
        for (int i = 0; i < 2; i++) {
            if (deck.cardsLeft() > 0) {
                hands[currPlayer].addCard(deck.deal());
            }
        }
    }

    public boolean getHasSkipped() {
        return hasSkipped;
    }

    public void setHasSkipped(boolean hasSkipped) {
        this.hasSkipped = hasSkipped;
    }
}
