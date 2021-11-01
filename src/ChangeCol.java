import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ChangeCol extends Card {
    public ChangeCol(String imgLoc) {
        super("", 4, -4, imgLoc);
    }

    public String toString() {
        return getColor() + " Change Color";
    }

    @Override
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
        setImage("UnoCards/" + col.toLowerCase() + "changecol.jpg");
    }
}
