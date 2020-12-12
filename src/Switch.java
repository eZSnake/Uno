public class Switch extends Card {
    public Switch(String col) {
        super(col, 1, 10);
    }

    public String toString() {
        return getColor() + " Switch";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
    }
}
