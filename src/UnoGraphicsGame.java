public class UnoGraphicsGame {
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, stackChangeCol = true, stackPlus = false;
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

    /**
     * Draws a card and also checks if the player is trying to draw while the bot is playing
     *
     * @param extPlayer the player who is trying to draw
     */
    public void draw(int extPlayer) {
        if (extPlayer == player) {
            hands[player].addCard(deck.deal());
        }
        nextPlayer();
    }

    public void draw() {
        draw(player);
    }

    /**
     * Plays a card by putting it on top of the place pile
     *
     * @param toPlay the card which is to be played
     */
    public void playCard(Card toPlay) {
        placePile = toPlay;
        switchSkip();
    }

    /**
     * Bot plays a card if it can otherwise it draws one
     */
    public void botPlayCard() {
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

    /**
     * Executes special move
     *
     * @param col the color to be changed to if it's a wish card or null if it isn't
     */
    public void doSpecialMove(String col) {
        if (stackPlus && placePile.toString().contains("+") && getPlayerHand(getNextPlayer()).hasPlus()) {
            //Dont do effect yet and see if player wants to stack on top
        }
        placePile.specialMove(deck, hands, player, rev, col);
        //TODO Add case for stacking plus cards so they add together (Just Plus 2 or also 4?)
    }

    /**
     *  Checks if everyone has more than 0 cards on their hand and that there are more than 0 cards left to draw
     *
     * @return if the round is playable
     */
    private boolean roundPlayable() {
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

    /**
     * Determines which player goes next, taking into account the direction and if the next player is supposed to be skipped
     *
     * @return the next player
     */
    public int getNextPlayer() {
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

    /**
     * Checks to make sure that skips and switches aren't done more than once if another card isn't placed on top
     */
    private void switchSkip() {
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

    /**
     * Checks if a player has a card to play
     *
     * @return if the current player has a playable card
     */
    private boolean canPlayACard() {
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).isPlayable(placePile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the selected card can be played
     *
     * @param toPlay the card to play
     * @return if the card can be played
     */
    public boolean canPlayCard(Card toPlay) {
        if (toPlay == null) {
            return false;
        }
        return toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum() || (toPlay.getId() == 4 && placePile.getId() == 4 && stackChangeCol);
        //TODO Fix implementation of not stacking Change Col cards
    }

    /**
     * Converts an inputted string to a card on the hand of the current player
     *
     * @param conv the String to convert to a card
     * @return the card which matches the inputted String
     */
    public Card stringToCard(String conv) {
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

    /**
     * Gets the amount of cards left in a player's hand
     *
     * @param plyr the player whose hand should be checked
     * @return the amount of cards the player has
     */
    public int getPCardsLeft(int plyr) {
        return hands[plyr].length();
    }

    /**
     * Gets the cards left in the draw pile
     *
     * @return the number of cards left in the draw pile
     */
    public int getCardsLeft() {
        return deck.cardsLeft();
    }

    /**
     * Gets the hand of the specified player
     *
     * @param plyr the player whose hand should be gotten
     * @return the hand of the specified player
     */
    public Hand getPlayerHand(int plyr) {
        return hands[plyr];
    }

    public Card getPlacePile() {
        return placePile;
    }

    public int getPlayer() {
        return player;
    }

    /**
     * Gets the index of a specified card
     *
     * @param toIndex the card whose index should be determined
     * @return the index of the specified card
     */
    public int getIndexOfCard(Card toIndex) {
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i) == toIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes a specified card from a player's hand
     *
     * @param toRemove the card to remove
     */
    public void removeCard(Card toRemove) {
        hands[player].removeCard(toRemove);
    }

    /**
     * Determines the amount of new cards added to a player's hand
     *
     * @return the number of new cards
     */
    public int newCardsAdded() {
        int diff = hands[player].length() - handLengths[player];
        updateHandLength();
        return diff;
    }

    /**
     * Gets everyone's hands
     *
     * @return an array of everyone's hands
     */
    public Hand[] getHands() {
        return hands;
    }

    /*
    //TODO Implement sorting of cards correctly
    public void sortHand() {
        ArrayList<Card> blue = new ArrayList<>(), green = new ArrayList<>(), yellow = new ArrayList<>(), red = new ArrayList<>(), black = new ArrayList<>();
        for (int i = 0; i < hands[player].length(); i++) {
            Card temp = hands[player].getCard(i);
            if (temp.getColor().equals("Blue") && temp.getId() == 0) {
                blue.add(temp);
            } else if (temp.getColor().equals("Green") && temp.getId() == 0) {
                green.add(temp);
            } else if (temp.getColor().equals("Yellow") && temp.getId() == 0) {
                yellow.add(temp);
            } else if (temp.getColor().equals("Red") && temp.getId() == 0) {
                red.add(temp);
            } else {
                black.add(temp);
            }
        }
        Card[] blueArr = new Card[blue.size()], greenArr = new Card[green.size()], yellowArr = new Card[yellow.size()], redArr = new Card[red.size()];
        blue.toArray(blueArr);
        green.toArray(greenArr);
        yellow.toArray(yellowArr);
        red.toArray(redArr);
        quickSort(blueArr, 0, blueArr.length);
        quickSort(greenArr, 0, greenArr.length);
        quickSort(yellowArr, 0, yellowArr.length);
        quickSort(redArr, 0, redArr.length);
    }

    public void quickSort(Card[] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(Card[] arr, int begin, int end) {
        int pivot = arr[end].getNum();
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].getNum() <= pivot) {
                i++;

                Card swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        Card swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }
    */

    public String getBotsPlay() {
        return botsPlay;
    }

    public void resetBotsPlay() {
        botsPlay = "";
    }

    public void setStackChangeCol(boolean toSetTo) {
        stackChangeCol = toSetTo;
    }

    public void setStackPlus(boolean toSetTo) {
        stackPlus = toSetTo;
    }

    /**
     * Determines the winner of the match if there is one
     *
     * @return an integer representing the winner or -1 if there is none
     */
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
