public interface Game {
    void playRounds();
    
    void draw(int extPlayer);

    int getPCardsLeft(int plyr);

    int getCardsLeft();

    Hand getPlayerHand();

    Deck getDeck();

    int determineWinner();
}
