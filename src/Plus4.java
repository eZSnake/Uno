import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Plus4 extends Card {
    private boolean hasSkipped;

    public Plus4(Image img) {
        super("", 4, -5, img);
        hasSkipped = false;
    }

    public String toString() {
        return getColor() + " +4 and Change Color";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer, boolean rev, String col) {
//        System.out.print("What would you like to change the color to? ");
//        if (col.equals("")) {
//            col = TextIO.getlnString().toLowerCase();
//            while (!col.equals("blue") && !col.equals("red") && !col.equals("green") && !col.equals("yellow")) {
//                System.out.print("Invalid color. Choose between Blue, Red, Green, Yellow. ");
//                col = TextIO.getlnString().toLowerCase();
//            }
//        }
        this.setColor(col.substring(0, 1).toUpperCase() + col.substring(1));
        Image img = null;
        try {
            img = ImageIO.read(new File("UnoCards/" + col.toLowerCase() + "plus4.jpg"));
        } catch (IOException ignored) {}
        setImage(img);
        if (rev) {
            currPlayer = Math.floorMod(currPlayer - 1, hands.length);
        } else {
            currPlayer = Math.floorMod(currPlayer + 1, hands.length);
        }
        for (int i = 0; i < 4; i++) {
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
