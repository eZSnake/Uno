public class BotUnoGame {  //Eike Rehwald
    private final Deck deck;
    private Card placePile;
    private final Hand[] hands;
    private boolean rev = false, skip = false, botTurn = false;
    private final BasicBot bot = new BasicBot();

    public BotUnoGame() {
        deck = new Deck();
        deck.shuffle(2);
        hands = new Hand[2];
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
        String cardToPlay, col;
        while (playable()) {
            skip = false;
            if (player == 0) {
                System.out.println("\n\n-=-= Your turn =-=-\n");
            } else {
                System.out.println("\n\n-=-= Bot's turn =-=-\n");
            }
            if (!canPlayACard(player)) {
                if (!botTurn) {
                    System.out.println("You do not have a card to play. A card will be automatically drawn for you");
                } else {
                    System.out.println("The bot doesn't have a playable card. A card will be drawn for it.");
                }
                hands[player].addCard(deck.deal());
                player = nextPlayer(player, hands.length);
                continue;
            }
            if (!botTurn) {
                System.out.print("Place pile card:\n" + placePile.toString()
                        + "\n\nYour cards:\n" + hands[player].toString(placePile)
                        + "\n\nWhat card would you like to play or would you like to draw? (1-" + hands[player].length() + "/'draw','d'): ");
                cardToPlay = TextIO.getlnString().toLowerCase();
            }
            else {
                //Uncomment the System.outs to see the bot's cards and what it chooses
//                System.out.println("Bot's cards:\n" + hands[1].toString(placePile));
                cardToPlay = bot.playCard(hands[1], placePile);
//                System.out.println("Bot's play: " + cardToPlay);
            }
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
            player = nextPlayer(player, hands.length);
        }
        System.out.print("\n\n-=-=-=-=-=-=-\n" + determineWinner() + "\n-=-=-=-=-=-=-");
    }

    private boolean playable() {
        //checks if everyone has more than 0 cards on their hand and that there are more than 0 cards left to draw
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
            botTurn = !botTurn;
        } else if (!rev){
            nextPlayer = Math.floorMod(currPlayer + 2, players);
        } else if (!skip) {
            nextPlayer = Math.floorMod(currPlayer - 1, players);
            botTurn = !botTurn;
        } else {
            nextPlayer = Math.floorMod(currPlayer-2, players);
        }
        return nextPlayer;
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

    private String determineWinner() {
        //determines which, if any, player has won or if the game is a draw
        if (hands[0].length() == 0) {
            return "You have won the game!\nCongratulations!";
        } else if (hands[1].length() == 0) {
            return "The Bot has won the game!\nBetter luck next time!";
        }
        return "There are no cards left to draw. It's a tie.";
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
