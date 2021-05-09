public class BotUnoGraphicsGame implements Game {
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, botTurn = false;
    private final BasicBot bot = new BasicBot();
    private int player = 0;

    public BotUnoGraphicsGame() {
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

    public void playRounds() {
        String cardToPlay, col;
        int cardInt = -1;
        while (roundPlayable()) {
            skip = false;
            if (!canPlayACard()) {
                draw();
                nextPlayer(hands.length);
                continue;
            }
            if (botTurn) {
                cardToPlay = bot.playCard(hands[1], placePile);
            }
            if (cardInt != -1) {
                placePile = hands[player].getCard(cardInt - 1);
                hands[player].removeCard(cardInt - 1);
            }
            if (hands[player].length() <= 0) {
                break;
            }
            if (botTurn) {
                col = bot.chooseColor(hands[1]);
            } else {
                col = "";
            }
            placePile.specialMove(deck, hands, player, col);
            switchSkip();
            if (placePile instanceof Switch) {
                if (!((Switch) placePile).getHasSwitched()) {
                    rev = !rev;
                    ((Switch) placePile).setHasSwitched(true);
                }
            }
        }
        determineWinner();
    }


    public void draw(int extPlayer) {
        //draws a card and also checks if the player is trying to draw while the bot is playing
        if (extPlayer == player) {
            hands[player].addCard(deck.deal());
        }
    }

    public void draw() {
        draw(player);
    }

    public void playCard(Card toPlay) {
        placePile = toPlay;
        hands[player].removeCard(toPlay);
    }

    private boolean roundPlayable() {
        //checks if everyone has more than 0 cards on their hand and that there are more than 0 cards left to draw
        for (Hand hand : hands) {
            if (hand.length() == 0) {
                return false;
            }
        }
        return deck.cardsLeft() >= 0;
    }

    private void nextPlayer(int players) {
        //determines which player goes next, taking into account the direction and if the next player is supposed to be skipped
        int nextPlayer;
        if (!rev && !skip) {
            nextPlayer = Math.floorMod(player + 1, players);
            botTurn = !botTurn;
        } else if (!rev){
            nextPlayer = Math.floorMod(player + 2, players);
        } else if (!skip) {
            nextPlayer = Math.floorMod(player - 1, players);
            botTurn = !botTurn;
        } else {
            nextPlayer = Math.floorMod(player - 2, players);
        }
        player = nextPlayer;
    }

    private boolean canPlayACard() {
        //checks if a player has a card to play
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).isPlayable(placePile)) {
                return true;
            }
        }
        return false;
    }

    private void switchSkip() {
        //checks to make sure that skips and switches aren't done more than once if another card isn't placed on top
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
        //convert string to card fisrt
        for (int i = 0; i < hands[player].length(); i++) {
            System.out.println(placePile.toString());
            //if (hands[player].getCard(i).toString().equals(toPlay)) {
            if (toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum()) {
                System.out.println("True");
                return true;
            }
        }
        System.out.println("False");
        return false;
    }

    public Card stringToCard(String conv) {
        Card card = null;
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).toString().equals(conv)) {
                card = hands[player].getCard(i);
            }
        }
        return card;
    }

    public int getPCardsLeft(int plyr) {
        return hands[plyr].length();
    }

    public int getCardsLeft() {
        return deck.cardsLeft();
    }

    public Hand getPlayerHand() {
        return hands[0];
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getPlacePile() {
        return placePile;
    }

    public int determineWinner() {
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
