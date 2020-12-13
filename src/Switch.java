public class Switch extends Card {
    private boolean hasSwitched;

    public Switch(String col) {
        super(col, 1, -1);
        hasSwitched = false;
    }

    public String toString() {
        return getColor() + " Switch";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
    }

    public boolean getHasSwitched() {
        return hasSwitched;
    }

    public void setHasSwitched(boolean hasSwitched) {
        this.hasSwitched = hasSwitched;
    }
}
