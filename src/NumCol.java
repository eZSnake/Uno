public class NumCol extends Card {
    public NumCol(String col, int num) {
        super(col, 0, num);
    }

    public String toString() {
        return getColor() + " " + getNum();
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
    }
}
