public class BasicBot {
    public BasicBot() {}

    public String playCard(Hand hand, Card placePile) {
        for (int i = 0; i < hand.length(); i++) {
            if (hand.getCard(i).isPlayable(placePile)) {
                return "" + (i+1);
            }
        }
        return "d";
    }

    public String chooseColor(Hand hand) {
        for (int i = 0; i < hand.length(); i++) {
            if (hand.getCard(i) instanceof NumCol) {
                return hand.getCard(i).getColor();
            }
        }
        int randCol = (int)(4*Math.random());
        switch (randCol) {
            case 0:
                return "Blue";
            case 1:
                return "Green";
            case 2:
                return "Red";
            default:
                return "Yellow";
        }
    }
}
