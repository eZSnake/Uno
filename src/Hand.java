import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<Card>();
    private boolean Star;

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

    public String toString() {
        Star = false;
        String ret = toString(null);
        Star = true;
        return ret;
    }

    public String toString(Card placePile) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < hand.size()-1; i++) {
            if (Star && hand.get(i).isPlayable(placePile)) {
                ret.append("(*) ");
            }
            ret.append("Card " + (i+1) + ": " + hand.get(i)).append("\n");
        }
        if (Star && hand.get(hand.size()-1).isPlayable(placePile)) {
            ret.append("(*) ");
        }
        ret.append("Card " + hand.size() + ": " + hand.get(hand.size()-1));
        return ret.toString();
    }

    public int length() {
        return hand.size();
    }
}
