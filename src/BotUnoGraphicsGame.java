public class BotUnoGraphicsGame { //implements Game
    private UnoListener listener;
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, botTurn = false; // botHasPlayed = false, playerHasPlayed = false;
    private final BasicBot bot = new BasicBot();
    private int player = 0;

    public BotUnoGraphicsGame(UnoListener listener) {
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
        this.listener = listener;
    }

    public void draw(int extPlayer) {
        //Draws a card and also checks if the player is trying to draw while the bot is playing
        if (extPlayer == player) {
            hands[player].addCard(deck.deal());
        }
        nextPlayer();
    }

    public void draw() {
        draw(player);
    }

    public void playCard(Card toPlay) {
        skip = false;
        placePile = toPlay;
        hands[player].removeCard(toPlay);
        switchSkip();
        if (placePile instanceof Switch) {
            if (!((Switch) placePile).getHasSwitched()) {
                rev = !rev;
                ((Switch) placePile).setHasSwitched(true);
            }
        }
        nextPlayer();
    }

    public void botPlayCard() {
//        try {wait(100);} catch(InterruptedException ignored) {}
        try {Thread.sleep(50);} catch(InterruptedException ignored) {}
        if (!canPlayACard()) {
            System.out.println("Bot drawing");
            draw();
        } else {
            String col = null;
            Card toPlay = bot.playCard(hands[1], placePile);
            if (toPlay.getId() == 4) {
                col = bot.chooseColor(hands[1]);
            }
            playCard(toPlay);
            doSpecialMove(col);
            if (getPlayer() == 1) {
                botPlayCard();
            }
        }
    }

    public void doSpecialMove(String col) {
        placePile.specialMove(deck, hands, player, col);
    }

    private boolean roundPlayable() {
        //Checks if everyone has more than 0 cards on their hand and that there are more than 0 cards left to draw
        for (Hand hand : hands) {
            if (hand.length() == 0) {
                return false;
            }
        }
        return deck.cardsLeft() >= 0;
    }

    private void nextPlayer() {
        //Determines which player goes next, taking into account the direction and if the next player is supposed to be skipped
        int nextPlayer;
        if (!rev && !skip) {
            nextPlayer = Math.floorMod(player + 1, hands.length);
            botTurn = !botTurn;
        } else if (!rev){
            nextPlayer = Math.floorMod(player + 2, hands.length);
        } else if (!skip) {
            nextPlayer = Math.floorMod(player - 1, hands.length);
            botTurn = !botTurn;
        } else {
            nextPlayer = Math.floorMod(player - 2, hands.length);
        }
        player = nextPlayer;
    }

    private boolean canPlayACard() {
        //Checks if a player has a card to play
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).isPlayable(placePile)) {
                return true;
            }
        }
        return false;
    }

    private void switchSkip() {
        //Checks to make sure that skips and switches aren't done more than once if another card isn't placed on top
        if (placePile instanceof Skip && !((Skip) placePile).getHasSkipped()) {
            skip = true;
            ((Skip) placePile).setHasSkipped(true);
        }
        if (placePile instanceof Plus2 && !((Plus2) placePile).getHasSkipped()) {
            skip = true;
            ((Plus2) placePile).setHasSkipped(true);
        }
        if (placePile instanceof Plus4 && !((Plus4) placePile).getHasSkipped()) {
            skip = true;
            ((Plus4) placePile).setHasSkipped(true);
        }
        if (placePile instanceof Switch && !((Switch) placePile).getHasSwitched()) {
            rev = !rev;
            ((Switch) placePile).setHasSwitched(true);
        }
    }

    public boolean canPlayCard(Card toPlay) {
        //Convert string to card fisrt
        for (int i = 0; i < hands[player].length(); i++) {
            if (toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum()) {
                return true;
            }
        }
        return false;
    }

    public Card stringToCard(String conv) {
        //Converts an inputted string to a card on the hand of the current player
        Card card = null;
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).toString().equals(conv)) {
                card = hands[player].getCard(i);
            }
        }
        return card;
    }

    //Methods to pass along information to the listener class and beyond
    public int getPCardsLeft(int plyr) {
        return hands[plyr].length();
    }

    public int getCardsLeft() {
        return deck.cardsLeft();
    }

    public Hand getPlayerHand(int plyr) {
        return hands[plyr];
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getPlacePile() {
        return placePile;
    }

    public int getPlayer() {
        return player;
    }

    public int determineWinner() {
        //Determines the winner, if there is one
        if (!roundPlayable()) {
            if (hands[0].length() == 0) {
                return 0;
            } else if (hands[1].length() == 0) {
                return 1;
            }
            return 2;
        }
        return -1;
    }
}
