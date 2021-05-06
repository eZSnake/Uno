public class BotUnoGraphics {
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, botTurn = false;
    private final BasicBot bot = new BasicBot();

    public BotUnoGraphics() {
        deck = new Deck();
        deck.shuffle(2);
        hands = new Hand[2];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new Hand();
            for (int j = 0; j < 7; j++) {
                hands[i].addCard(deck.deal());
            }
        }
        placePile = deck.deal();
    }

    public void draw() {
        hands[0].addCard(deck.deal()); //0 placeholder
    }
}
