public class UnoNetData {
    private Card placePile;
    private Hand[] hands;
    private int currPlayer, playerCount, cardsInDrawPile;
    private String specialMove;

    UnoNetData(Card placePile, Hand[] hands, int currPlayer, int totPlayers, String specialMove, int cardsInDrawPile) {
        this.placePile = placePile;
        this.hands = hands;
        this.currPlayer = currPlayer;
        this.playerCount = totPlayers;
        this.specialMove = specialMove;
        this.cardsInDrawPile = cardsInDrawPile;
    }

    public Card getPlacePile() {
        return placePile;
    }

    public Hand getHand(int handToGet) {
        return hands[handToGet];
    }

    public int getCardsLeft(int plyr) {
        return hands[plyr].length();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getCardsInDrawPile() {
        return cardsInDrawPile;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public String getSpecialMove() {
        return specialMove;
    }
}
