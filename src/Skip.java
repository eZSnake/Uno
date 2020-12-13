public class Skip extends Card {
    boolean hasSkipped;

    public Skip(String col) {
        super(col, 2, -2);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " Skip";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) { }

    public boolean getHasSkipped() {
        return hasSkipped;
    }

    public void setHasSkipped(boolean hasSkipped) {
        this.hasSkipped = hasSkipped;
    }
}
