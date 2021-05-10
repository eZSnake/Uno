import java.awt.*;

public abstract class Card {
    private int num, id;
    private String color;
    private Image image;

    public Card(String col, int id, int num, Image img) {
        color = col;
        this.id = id;
        this.num = num;
        image = img;
    }

    public abstract String toString();

    public abstract void specialMove(Deck deck, Hand[] hands, int currPlayer, String col);

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image img) {
        image = img;
    }

    public boolean isPlayable(Card placePile) {
        return (id == 4 || color.equals(placePile.getColor()) || num == placePile.getNum());
    }
}
