import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Deck {
    private int cardsDealt = 0;
    private final Card[] cards = new Card[108];
    private BufferedImage img;

    public Deck() {
        int i;
        //Blue cards
        int numToSet = 1;
        for (i = 0; i < 19; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("blue" + i + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol("Blue", numToSet, img);
            numToSet++;
        }
        for (i = 19; i < 21; i++) {
            try {
                img = ImageIO.read(new File("blueskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip("Blue", img);
        }
        for (i = 21; i < 23; i++) {
            try {
                img = ImageIO.read(new File("blueswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch("Blue", img);
        }
        for (i = 23; i < 25; i++) {
            try {
                img = ImageIO.read(new File("blueplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2("Blue", img);
        }
        //Green cards
        numToSet = 1;
        for (i = 25; i < 44; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("green" + i + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol("Green", numToSet, img);
            numToSet++;
        }
        for (i = 44; i < 46; i++) {
            try {
                img = ImageIO.read(new File("greenskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip("Green", img);
        }
        for (i = 46; i < 48; i++) {
            try {
                img = ImageIO.read(new File("greenswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch("Green", img);
        }
        for (i = 48; i < 50; i++) {
            try {
                img = ImageIO.read(new File("greenplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2("Green", img);
        }
        //Red cards
        numToSet = 1;
        for (i = 50; i < 69; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("red" + i + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol("Red", numToSet, img);
            numToSet++;
        }
        for (i = 69; i < 71; i++) {
            try {
                img = ImageIO.read(new File("redskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip("Red", img);
        }
        for (i = 71; i < 73; i++) {
            try {
                img = ImageIO.read(new File("redswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch("Red", img);
        }
        for (i = 73; i < 75; i++) {
            try {
                img = ImageIO.read(new File("redplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2("Red", img);
        }
        //Yellow cards
        numToSet = 1;
        for (i = 75; i < 96; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("yellow" + i + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol("Yellow", numToSet, img);
            numToSet++;
        }
        for (i = 94; i < 96; i++) {
            try {
                img = ImageIO.read(new File("yellowskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip("Yellow", img);
        }
        for (i = 96; i < 98; i++) {
            try {
                img = ImageIO.read(new File("yellowswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch("Yellow", img);
        }
        for (i = 98; i < 100; i++) {
            try {
                img = ImageIO.read(new File("yellowplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2("Yellow", img);
        }
        //Special cards
        for (i = 100; i < 104; i++) {
            try {
                img = ImageIO.read(new File("changecol.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new ChangeCol(img);
        }
        for (i = 104; i < 108; i++) {
            try {
                img = ImageIO.read(new File("plus4.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus4(img);
        }
//        printDeck();  //Uncomment to print out the entire deck at the beginning
    }

    public void shuffle(int players) {
        Random random = new Random();
        for (int i = 0; i < cards.length-1; i++) {
            int randVal = i + random.nextInt(cards.length-i);
            Card temp = cards[randVal];
            cards[randVal] = cards[i];
            cards[i] = temp;
        }
        if (!(cards[players*7] instanceof NumCol)) {
            for (int i = players*7; i < cards.length; i++) {
                if (cards[i] instanceof  NumCol) {
                    Card temp = cards[players*7];
                    cards[players*7] = cards[i];
                    cards[i] = temp;
                }
            }
        }
    }

    public Card deal() {
        cardsDealt++;
        return cards[cardsDealt-1];
    }

    public int cardsLeft() {
        return 108-cardsDealt;
    }

    public void printDeck() {
        for (int i = 0; i < 108; i++) {
            System.out.println(cards[i].toString());
        }
    }
}
