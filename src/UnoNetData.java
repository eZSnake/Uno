public class UnoNetData {
    private Card placePile;
    private Hand[] hands;
    private int[] handLengths;
    private int currPlayer;
    private String specialMove;

    UnoNetData(Card placePile, Hand[] hands, int[] handLengths, int currPlayer, String specialMove) {
        this.placePile = placePile;
        this.hands = hands;
        this.handLengths = handLengths;
        this.currPlayer = currPlayer;
        this.specialMove = specialMove;
    }

    public Card getPlacePile() {
        return placePile;
    }

    public Hand getHand(int handToGet) {
        return hands[handToGet];
    }

    public int[] getHandLengths() {
        return handLengths;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public String getSpecialMove() {
        return specialMove;
    }
}
