import java.util.Random;

public class Deck {
    private int cardsDealt = 0;
    private final Card[] cards = new Card[108];

    public Deck() {
        int i;
        //Blue cards
        int numToSet = 1;
        for (i = 0; i < 19; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol("Blue", numToSet);
            numToSet++;
        }
        for (i = 19; i < 21; i++) {
            cards[i] = new Skip("Blue");
        }
        for (i = 21; i < 23; i++) {
            cards[i] = new Switch("Blue");
        }
        for (i = 23; i < 25; i++) {
            cards[i] = new Plus2("Blue");
        }
        //Green cards
        numToSet = 1;
        for (i = 25; i < 44; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol("Green", numToSet);
            numToSet++;
        }
        for (i = 44; i < 46; i++) {
            cards[i] = new Skip("Green");
        }
        for (i = 46; i < 48; i++) {
            cards[i] = new Switch("Green");
        }
        for (i = 48; i < 50; i++) {
            cards[i] = new Plus2("Green");
        }
        //Red cards
        numToSet = 1;
        for (i = 50; i < 69; i++) {
            if (i%2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol("Red", numToSet);
            numToSet++;
        }
        for (i = 69; i < 71; i++) {
            cards[i] = new Skip("Red");
        }
        for (i = 71; i < 73; i++) {
            cards[i] = new Switch("Red");
        }
        for (i = 73; i < 75; i++) {
            cards[i] = new Plus2("Red");
        }
        //Yellow cards
        numToSet = 1;
        for (i = 75; i < 96; i++) {
            if ((i-1)%2 == 0) {
                numToSet--;
            }
            cards[i] = new NumCol("Yellow", numToSet);
            numToSet++;
        }
        for (i = 94; i < 96; i++) {
            cards[i] = new Skip("Yellow");
        }
        for (i = 96; i < 98; i++) {
            cards[i] = new Switch("Yellow");
        }
        for (i = 98; i < 100; i++) {
            cards[i] = new Plus2("Yellow");
        }
        //Special cards
        for (i = 100; i < 104; i++) {
            cards[i] = new ChangeCol();
        }
        for (i = 104; i < 108; i++) {
            cards[i] = new Plus4();
        }
        printDeck();
    }

    public void shuffle(int players) {
        Random random = new Random();
        for (int i = 0; i < cards.length-1; i++) {
            int randVal = i + random.nextInt(cards.length-i);
            Card temp = cards[randVal];
            cards[randVal] = cards[i];
            cards[i] = temp;
        }
        if (!(cards[players*7+1] instanceof NumCol)) {
            for (int i = players*7+1; i < cards.length; i++) {
                if (cards[i] instanceof  NumCol) {
                    Card temp = cards[players*7+1];
                    cards[players*7+1] = cards[i];
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
