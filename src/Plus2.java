public class Plus2 extends Card {
    private boolean hasSkipped;

    public Plus2(String col) {
        super(col, 3, 10);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " +2";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
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
