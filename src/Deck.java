import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Deck {
    private int cardsDealt = 0;
    private final Card[] cards = new Card[108];

    public Deck() {
        int i;
        // Blue cards
        int numToSet = 1;
        String BLUE = "Blue";
        for (i = 0; i < 19; i++) {
            if (i % 2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol(BLUE, numToSet, "UnoCards/blue" + numToSet + ".jpg");
            numToSet++;
        }
        for (i = 19; i < 21; i++) {
            cards[i] = new Skip(BLUE, "UnoCards/blueskip.jpg");
        }
        for (i = 21; i < 23; i++) {
            cards[i] = new Switch(BLUE, "UnoCards/blueswitch.jpg");
        }
        for (i = 23; i < 25; i++) {
            cards[i] = new Plus2(BLUE, "UnoCards/blueplus2.jpg");
        }
        // Green cards
        numToSet = 1;
        String GREEN = "Green";
        for (i = 25; i < 44; i++) {
            if ((i - 1) % 2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol(GREEN, numToSet, "UnoCards/green" + numToSet + ".jpg");
            numToSet++;
        }
        for (i = 44; i < 46; i++) {
            cards[i] = new Skip(GREEN, "UnoCards/greenskip.jpg");
        }
        for (i = 46; i < 48; i++) {
            cards[i] = new Switch(GREEN, "UnoCards/greenswitch.jpg");
        }
        for (i = 48; i < 50; i++) {
            cards[i] = new Plus2(GREEN, "UnoCards/greenplus2.jpg");
        }
        // Red cards
        numToSet = 1;
        String RED = "Red";
        for (i = 50; i < 69; i++) {
            if (i % 2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol(RED, numToSet, "UnoCards/red" + numToSet + ".jpg");
            numToSet++;
        }
        for (i = 69; i < 71; i++) {
            cards[i] = new Skip(RED, "UnoCards/redskip.jpg");
        }
        for (i = 71; i < 73; i++) {
            cards[i] = new Switch(RED, "UnoCards/redswitch.jpg");
        }
        for (i = 73; i < 75; i++) {
            cards[i] = new Plus2(RED, "UnoCards/redplus2.jpg");
        }
        // Yellow cards
        numToSet = 1;
        String YELLOW = "Yellow";
        for (i = 75; i < 94; i++) {
            if ((i - 1) % 2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol(YELLOW, numToSet, "UnoCards/yellow" + numToSet + ".jpg");
            numToSet++;
        }
        for (i = 94; i < 96; i++) {
            cards[i] = new Skip(YELLOW, "UnoCards/yellowskip.jpg");
        }
        for (i = 96; i < 98; i++) {
            cards[i] = new Switch(YELLOW, "UnoCards/yellowswitch.jpg");
        }
        for (i = 98; i < 100; i++) {
            cards[i] = new Plus2(YELLOW, "UnoCards/yellowplus2.jpg");
        }
        // Special cards
        for (i = 100; i < 104; i++) {
            cards[i] = new ChangeCol("UnoCards/changecol.jpg");
        }
        for (i = 104; i < 108; i++) {
            cards[i] = new Plus4("UnoCards/plus4.jpg");
        }
//        printDeck();  //Uncomment to print out the entire deck at the beginning
    }

    public void shuffle(int players) {
        for (int i = 0; i < cards.length - 1; i++) {
            int randVal = i + ThreadLocalRandom.current().nextInt(cards.length-i);
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
