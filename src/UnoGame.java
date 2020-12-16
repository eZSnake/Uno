public class UnoGame { //Eike Rehwald
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false;

    public UnoGame(int players) {
        deck = new Deck();
        deck.shuffle(players);
        hands = new Hand[players];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new Hand();
            for (int j = 0; j < 7; j++) {
                hands[i].addCard(deck.deal());
            }
        }
        //Uncomment below to print out all player's starting hands
//        for (int i = 0; i < hands.length; i++) {
//            System.out.println("Player " + (i+1) + "'s Hand:");
//            System.out.println(hands[i].toString());
//        }
        placePile = deck.deal();
    }

    public void playRounds(int player) {
        while (playable()) {
            skip = false;
            System.out.println("\n\n-=-= Player " + (player + 1) + "'s turn =-=-\n");
            if (!canPlayACard(player)) {
                System.out.println("You do not have a card to play. A card will be automatically drawn for you");
                hands[player].addCard(deck.deal());
                player = nextPlayer(player, hands.length);
                continue;
            }
            System.out.print("Place pile card:\n" + placePile.toString()
                    + "\n\nYour cards:\n" + hands[player].toString(placePile)
                    + "\n\nWhat card would you like to play or would you like to draw? (1-" + hands[player].length() + "/'draw','d'): ");
            String cardToPlay = TextIO.getlnString().toLowerCase();
            boolean actionTaken = false;
            int cardInt = -1;
            while (!actionTaken) {
                if (cardToPlay.equals("draw") || cardToPlay.equals("d")) {
                    hands[player].addCard(deck.deal());
                    actionTaken = true;
                } else if (isInt(cardToPlay)) {
                    cardInt = Integer.parseInt(cardToPlay);
                    if (cardInt < 1 || cardInt > hands[player].length() || !(hands[player].getCard(cardInt-1)).isPlayable(placePile)) {
                        System.out.print("That is an invalid entry. Enter one between 1 and " + hands[player].length() + " or 'draw'/'d': ");
                        cardToPlay = TextIO.getlnString();
                        cardInt = -1;
                    } else {
                        actionTaken = true;
                    }
                } else {
                    System.out.print("That isn't a valid value. Enter 'draw', 'd', or a number from 1 to " + hands[player].length() + ": ");
                    cardToPlay = TextIO.getlnString();
                }
            }
            if (cardInt != -1) {
                placePile = hands[player].getCard(cardInt - 1);
                hands[player].removeCard(cardInt - 1);
            }
            if (hands[player].length() <= 0) {
                break;
            }
            placePile.specialMove(deck, hands, player, "");
            switchSkip();
            player = nextPlayer(player, hands.length);
        }
        System.out.print("\n\n-=-=-=-=-=-=-\n" + determineWinner() + "\n-=-=-=-=-=-=-");
    }

    private boolean playable() {
        for (int i = 0; i < hands.length; i++) {
            if (hands[i].length() == 0) {
                return false;
            }
        }
        return deck.cardsLeft() >= 0;
    }

    private boolean isInt(String card) {
        //determines if the value the player has selected for which card to play is an integer
        try {
            Integer.parseInt(card);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private int nextPlayer(int currPlayer, int players) {
        //determines which player goes next, taking into account the direction and if the next player is supposed to be skipped
        int nextPlayer;
        if (!rev && !skip) {
            nextPlayer = Math.floorMod(currPlayer + 1, players);
        } else if (!rev){
            nextPlayer = Math.floorMod(currPlayer + 2, players);
        } else if (!skip) {
            nextPlayer = Math.floorMod(currPlayer - 1, players);
        } else {
            nextPlayer = Math.floorMod(currPlayer-2, players);
        }
        return nextPlayer;
    }

    private String determineWinner() {
        //determines which, if any, player has won or if the game is a draw
        for (int i = 0; i < hands.length; i++) {
            if (hands[i].length() == 0) {
                return "Player " + (i+1) + " has won the game!\nCongratulations!";
            }
        }
        return "There are no cards left to draw. It's a tie.";
    }

    private boolean canPlayACard(int player) {
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
}
