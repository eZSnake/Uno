import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Card {
    private int num, id;
    private String color, imgLoc;

    protected Card(String col, int id, int num, String imgLoc) {
        color = col;
        this.id = id;
        this.num = num;
        this.imgLoc = imgLoc;
    }

    public abstract String toString();

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, boolean rev, String col) {}

    public int getNum() {
        return num;
    }

    public int getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Image getImage() {
        try {
            return ImageIO.read(new File("UnoCards/" + imgLoc + ".jpg"));
        } catch (IOException imgReadFailed) {
            return null;
        }
    }

    public void setImage(String newImgLoc) {
        imgLoc = newImgLoc;
    }

    public boolean isPlayable(Card placePile) {
        return (id == 4 || color.equals(placePile.getColor()) || num == placePile.getNum());
    }
}
