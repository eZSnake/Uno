import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<Card>();

    public Hand() {

    }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void removeCard(int index) {
        hand.remove(index-1);
    }

    public Card getCard(int index) {
        return hand.get(index-1);
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < hand.size()-1; i++) {
            ret.append("Card " + (i+1) + ": " + hand.get(i)).append("\n");
        }
        ret.append("Card " + hand.size() + ": " + hand.get(hand.size()-1));
        return ret.toString();
    }

    public int length() {
        return hand.size();
    }
}
