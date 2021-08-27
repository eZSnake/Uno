public class UnoNetData {
    private Card placePile;
    private Hand[] hands;
    private int currPlayer, playerCount, cardsInDrawPile;

    UnoNetData(Card placePile, Hand[] hands, int currPlayer, int totPlayers, int cardsInDrawPile) {
        this.placePile = placePile;
        this.hands = hands;
        this.currPlayer = currPlayer;
        this.playerCount = totPlayers;
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
}
