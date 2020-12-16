public abstract class Card {
    private int num, id;
    private String color;

    public Card(String col, int id, int num) {
        color = col;
        this.id = id;
        this.num = num;
    }

    public abstract String toString();

    public abstract void specialMove(Deck deck, Hand[] hands, int currPlayer);

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isPlayable(Card placePile) {
        return (id == 4 || color.equals(placePile.getColor()) || num == placePile.getNum());
    }
}
