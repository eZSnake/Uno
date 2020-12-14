public class UnoGame {
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
        for (int i = 0; i < hands.length; i++) {
            System.out.println(hands[i].toString());
        }
        placePile = deck.deal();
    }

    public void playRound(int player) {
        while (playable()) {
            skip = false;
            System.out.println("\n\n-=-= Player " + (player + 1) + "'s turn =-=-\n");
            if (!canPlayACard(player)) {
                System.out.println("You do not have a card to play. A card will be automatically drawn for you");
                hands[player].addCard(deck.deal());
                player = nextPlayer(player, hands.length);
                continue;
            }
            System.out.println("Place pile card:\n" + placePile.toString());
            System.out.println("\nYour cards:\n" + hands[player].toString());
            System.out.print("\nWhat card would you like to play or would you like to draw? (1-" + hands[player].length() + "/'draw','d'): ");
            String cardToPlay = TextIO.getlnString().toLowerCase();
            boolean actionTaken = false;
            int cardInt = -1;
            while (!actionTaken) {
                if (isInt(cardToPlay)) {
                    cardInt = Integer.parseInt(cardToPlay);
                    if (cardInt < 1 || cardInt > hands[player].length()) {
                        System.out.print("That is an invalid number. Enter one between 1 and " + hands[player].length() + " or 'draw'/'d': ");
                        cardToPlay = TextIO.getlnString();
                        cardInt = -1;
                    } else if (!(hands[player].getCard(cardInt-1)).isPlayable(placePile)) {
                        System.out.print("That card can't be played. Enter another one or 'draw'/'d': ");
                        cardToPlay = TextIO.getlnString();
                        cardInt = -1;
                    } else {
                        actionTaken = true;
                    }
                } else if (cardToPlay.equals("draw") || cardToPlay.equals("d")) {
                    hands[player].addCard(deck.deal());
                    actionTaken = true;
                } else {
                    System.out.print("That isn't a valid value. Enter 'draw', 'd', or a number from 1 to " + hands[player].length() + ": ");
                    cardToPlay = TextIO.getlnString();
                }
            }
            if (cardInt != -1) {
                placePile = hands[player].getCard(cardInt-1);
                hands[player].removeCard(cardInt-1);
                if (hands[player].length() <= 0) {
                    break;
                }
                placePile.specialMove(deck, hands, player);
            }
            if (placePile instanceof Skip || placePile instanceof Plus2 || placePile instanceof Plus4) {
                if (placePile instanceof Skip) {
                    if (!((Skip) placePile).getHasSkipped()) {
                        skip = true;
                        ((Skip) placePile).setHasSkipped(true);
                    }
                } else if (placePile instanceof Plus2) {
                    if (!((Plus2) placePile).getHasSkipped()) {
                        skip = true;
                        ((Plus2) placePile).setHasSkipped(true);
                    }
                } else {
                    if (!((Plus4) placePile).getHasSkipped()) {
                        skip = true;
                        ((Plus4) placePile).setHasSkipped(true);
                    }
                }
            }
            if (placePile instanceof Switch) {
                if (!((Switch) placePile).getHasSwitched()) {
                    rev = !rev;
                    ((Switch) placePile).setHasSwitched(true);
                }
            }
            player = nextPlayer(player, hands.length);
        }
        System.out.print("\n-=-=-=-=-=-=-\n" + determineWinner() + "\n-=-=-=-=-=-=-");
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
        try {
            Integer.parseInt(card);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private int nextPlayer(int currPlayer, int players) {
        int nextPlayer;
        if (!rev && !skip) {
            nextPlayer = Math.floorMod(currPlayer + 1, players);
        } else if (!rev && skip){
            nextPlayer = Math.floorMod(currPlayer + 2, players);
        } else if (rev && !skip) {
            nextPlayer = Math.floorMod(currPlayer - 1, players);
        } else {
            nextPlayer = Math.floorMod(currPlayer-2, players);
        }
        return nextPlayer;
    }

    private String determineWinner() {
        for (int i = 0; i < hands.length; i++) {
            if (hands[i].length() == 0) {
                return "Player " + (i+1) + " has won the game!";
            }
        }
        return "There are no cards left to draw. It's a tie.";
    }

    private boolean canPlayACard(int player) {
        for (int i = 0; i < hands[player].length(); i++) {
            if (hands[player].getCard(i).isPlayable(placePile)) {
                return true;
            }
        }
        return false;
    }
}
