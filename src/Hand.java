import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<Card>();

    public Hand() { }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void removeCard(int index) {
        hand.remove(index);
    }

    public Card getCard(int index) {
        return hand.get(index);
    }

    public String toString(Card placePile) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < hand.size()-1; i++) {
            if (hand.get(i).isPlayable(placePile)) {
                ret.append("(*) ");
            }
            ret.append("Card " + (i+1) + ": " + hand.get(i)).append("\n");
        }
        if (hand.get(hand.size()-1).isPlayable(placePile)) {
            ret.append("(*) ");
        }
        ret.append("Card " + hand.size() + ": " + hand.get(hand.size()-1));
        return ret.toString();
    }

    public int length() {
        return hand.size();
    }
}
