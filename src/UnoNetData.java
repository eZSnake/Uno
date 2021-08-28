public class UnoNetData {
    private Card placePile, toPlay;
    private Hand[] hands;
    private int currPlayer, playerCount, cardsInDrawPile;
    private String specialMove;

    UnoNetData(Card placePile, Card toPlay, Hand[] hands, int currPlayer, int totPlayers, int cardsInDrawPile, String specialMove) {
        this.placePile = placePile;
        this.toPlay = toPlay;
        this.hands = hands;
        this.currPlayer = currPlayer;
        this.playerCount = totPlayers;
        this.cardsInDrawPile = cardsInDrawPile;
        this.specialMove = specialMove;
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

    public Card getToPlay() {
        return toPlay;
    }

    public String getSpecialMove() {
        return specialMove;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }
}
