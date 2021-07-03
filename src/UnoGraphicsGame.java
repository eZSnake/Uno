public class UnoGraphicsGame {
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, botGame, stackChangeCol = true, stackPlus = false;
    private final BasicBot bot = new BasicBot();
    private int player = 0, players;
    private int[] handLengths;
    private String botsPlay = "";

    public UnoGraphicsGame(int players) {
        this.players = players;
        handLengths = new int[players];
        deck = new Deck();
        deck.shuffle(players);
        hands = new Hand[players];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new Hand();
            for (int j = 0; j < 7; j++) {
                hands[i].addCard(deck.deal());
            }
            handLengths[i] = hands[i].length();
        }
        placePile = deck.deal();
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
        //Puts a card on top of the place pile
        placePile = toPlay;
        switchSkip();
    }

    public void botPlayCard() {
        //Bot plays a card if it can otherwise it draws one
        if (!canPlayACard()) {
            if (botsPlay.contains("+") || botsPlay.contains("Skip")) {
                botsPlay += ", Draw";
            } else {
                botsPlay = "Draw";
            }
            draw();
        } else {
            String col = null;
            Card toPlay = bot.playCard(hands[1], placePile);
            if (toPlay.getId() == 4) {
                col = bot.chooseColor(hands[1]);
            }
            if (botsPlay.contains("+") || botsPlay.contains("Skip")) {
                botsPlay += ", ";
                if (toPlay.getId() == 4) {
                    botsPlay += col;
                }
                botsPlay += toPlay;
            } else {
                if (toPlay.getId() == 4) {
                    botsPlay += col + " " + toPlay;
                } else {
                    botsPlay = toPlay.toString();
                }
            }
            playCard(toPlay);
            doSpecialMove(col);
            hands[1].removeCard(toPlay);
            nextPlayer();
            if (getPlayer() == 1) {
                botPlayCard();
            }
        }
    }

    public void doSpecialMove(String col) {
        //Executes special move
        if (stackPlus && placePile.toString().contains("+") && getPlayerHand(getNextPlayer()).hasPlus()) {
            //Dont do effect yet and see if player wants to stack on top
        }
        placePile.specialMove(deck, hands, player, rev, col);
        //TODO Add case for stacking plus cards so they add together (Just Plus 2 or also 4?)
    }

    private boolean roundPlayable() {
        //Checks if everyone has more than 0 cards on their hand and that there are more than 0 cards left to draw
        for (Hand hand : hands) {
            if (hand.length() == 0) {
                return false;
            }
        }
        return deck.cardsLeft() > 0;
    }

    private void updateHandLength() {
        handLengths[player] = hands[player].length();
    }

    public int getNextPlayer() {
        //Determines which player goes next, taking into account the direction and if the next player is supposed to be skipped
        int nextPlayer;
        if (!rev && !skip) {
            nextPlayer = Math.floorMod(player + 1, hands.length);
        } else if (!rev){
            nextPlayer = Math.floorMod(player + 2, hands.length);
        } else if (!skip) {
            nextPlayer = Math.floorMod(player - 1, hands.length);
        } else {
            nextPlayer = Math.floorMod(player - 2, hands.length);
        }
        skip = false;

        return nextPlayer;
    }

    public void nextPlayer() {
        player = getNextPlayer();
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

    private boolean canPlayACard() {
        //Checks if a player has a card to play
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).isPlayable(placePile)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPlayCard(Card toPlay) {
        //Checks if card can be played
        if (toPlay == null) {
            return false;
        }
        return toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum() || (toPlay.getId() == 4 && placePile.getId() == 4 && stackChangeCol);
        //TODO Fix implementation of not stacking Change Col cards
    }

    public Card stringToCard(String conv) {
        //Converts an inputted string to a card on the hand of the current player
        try {
            for (int i = 0; i < hands[player].length(); i++) {
                if (hands[player].getCard(i).toString().equals(conv)) {
                    return hands[player].getCard(i);
                }
            }
            throw new Exception("FATAL - in stringToCard - card not member of hand");
        } catch (Exception ignored) {}
        return null;
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

    public Card getPlacePile() {
        return placePile;
    }

    public int getPlayer() {
        return player;
    }

    public int getIndexOfCard(Card toIndex) {
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i) == toIndex) {
                return i;
            }
        }
        return -1;
    }

    public void removeCard(Card toRemove) {
        hands[player].removeCard(toRemove);
    }

    public int newCardsAdded() {
        int diff = hands[player].length() - handLengths[player];
        updateHandLength();
        return diff;
    }

    public String getBotsPlay() {
        return botsPlay;
    }

    public void resetBotsPlay() {
        botsPlay = "";
    }

    public void setBotGame(boolean isBotGame) {
        botGame = isBotGame;
    }

    public void setStackChangeCol(boolean toSetTo) {
        stackChangeCol = toSetTo;
    }

    public void setStackPlus(boolean toSetTo) {
        stackPlus = toSetTo;
    }

    public int determineWinner() {
        //Determines the winner, if there is one
        if (!roundPlayable()) {
            for (int i = 0; i < players; i++) {
                if (hands[i].length() == 0) {
                    return i;
                }
            }
            return 4;
        }
        return -1;
    }
}
