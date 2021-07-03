import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<Card>();
    private boolean Star = true;

    public Hand() { }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void removeCard(int index) {
        hand.remove(index);
    }

    public void removeCard(Card c) {
        hand.remove(c);
    }

    public Card getCard(int index) {
        return hand.get(index);
    }

    public boolean hasPlus() {
        for (Card card : hand) {
            if (card.toString().contains("+")) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        Star = false;
        String ret = toString(null);
        Star = true;
        return ret;
    }

    public String toString(Card placePile) {
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size()-1; i++) {
            if (Star && hand.get(i).isPlayable(placePile)) {
                handCards.append("(*) ");
            }
            handCards.append("Card ").append(i + 1).append(": ").append(hand.get(i)).append("\n");
        }
        if (Star && hand.get(hand.size()-1).isPlayable(placePile)) {
            handCards.append("(*) ");
        }
        handCards.append("Card ").append(hand.size()).append(": ").append(hand.get(hand.size() - 1));
        return handCards.toString();
    }

    public int length() {
        return hand.size();
    }
}
