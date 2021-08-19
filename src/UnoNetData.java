public class UnoNetData {
    private Card placePile;
    private Hand hand;
    private int[] handLengths;

    UnoNetData(Card placePile, Hand hand, int[] handLengths) {
        this.placePile = placePile;
        this.hand = hand;
        this.handLengths = handLengths;
    }
}
