import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Deck {
    private int cardsDealt = 0;
    private final Card[] cards = new Card[108];
    private Image img;

    public Deck() {
        int i;
        // Blue cards
        int numToSet = 1;
        String BLUE = "Blue";
        for (i = 0; i < 19; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("UnoCards/blue" + numToSet + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol(BLUE, numToSet, img);
            numToSet++;
        }
        for (i = 19; i < 21; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/blueskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip(BLUE, img);
        }
        for (i = 21; i < 23; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/blueswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch(BLUE, img);
        }
        for (i = 23; i < 25; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/blueplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2(BLUE, img);
        }
        // Green cards
        numToSet = 1;
        String GREEN = "Green";
        for (i = 25; i < 44; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("UnoCards/green" + numToSet + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol(GREEN, numToSet, img);
            numToSet++;
        }
        for (i = 44; i < 46; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/greenskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip(GREEN, img);
        }
        for (i = 46; i < 48; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/greenswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch(GREEN, img);
        }
        for (i = 48; i < 50; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/greenplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2(GREEN, img);
        }
        // Red cards
        numToSet = 1;
        String RED = "Red";
        for (i = 50; i < 69; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("UnoCards/red" + numToSet + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol(RED, numToSet, img);
            numToSet++;
        }
        for (i = 69; i < 71; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/redskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip(RED, img);
        }
        for (i = 71; i < 73; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/redswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch(RED, img);
        }
        for (i = 73; i < 75; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/redplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2(RED, img);
        }
        // Yellow cards
        numToSet = 1;
        String YELLOW = "Yellow";
        for (i = 75; i < 94; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            try {
                img = ImageIO.read(new File("UnoCards/yellow" + numToSet + ".jpg"));
            } catch (IOException ignored) {}
            cards[i] = new NumCol(YELLOW, numToSet, img);
            numToSet++;
        }
        for (i = 94; i < 96; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/yellowskip.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Skip(YELLOW, img);
        }
        for (i = 96; i < 98; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/yellowswitch.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Switch(YELLOW, img);
        }
        for (i = 98; i < 100; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/yellowplus2.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus2(YELLOW, img);
        }
        // Special cards
        for (i = 100; i < 104; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/changecol.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new ChangeCol(img);
        }
        for (i = 104; i < 108; i++) {
            try {
                img = ImageIO.read(new File("UnoCards/plus4.jpg"));
            } catch (IOException ignored) {}
            cards[i] = new Plus4(img);
        }
//        printDeck();  //Uncomment to print out the entire deck at the beginning
    }

    public void shuffle(int players) {
        Random random = new Random();
        for (int i = 0; i < cards.length - 1; i++) {
            int randVal = i + random.nextInt(cards.length-i);
            Card temp = cards[randVal];
            cards[randVal] = cards[i];
            cards[i] = temp;
        }
        if (!(cards[players * 7] instanceof NumCol)) {
            for (int i = players * 7; i < cards.length; i++) {
                if (cards[i] instanceof  NumCol) {
                    Card temp = cards[players * 7];
                    cards[players * 7] = cards[i];
                    cards[i] = temp;
                }
            }
        }
    }

    public Card deal() {
        cardsDealt++;
        return cards[cardsDealt - 1];
    }

    public int cardsLeft() {
        return 108 - cardsDealt;
    }

    public void printDeck() {
        for (int i = 0; i < 108; i++) {
            System.out.println((i + 1) + ": " + cards[i].toString());
        }
    }

    public Card[] getCards() {
        return cards;
    }
}
