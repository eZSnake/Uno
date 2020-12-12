public class Skip extends Card {
    public Skip(String col) {
        super(col, 2, 10);
    }

    public String toString() {
        return getColor() + " Skip";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
    }
}
