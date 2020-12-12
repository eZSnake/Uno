import javax.swing.*;

public class UnoGame {
    private Deck deck;
    private Card placePile;
    private Hand[] hands;
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
//        if (rev) {
//            player = hands.length-player;
//        }
//        if (!skip) {
            System.out.println("\n-=-= Player " + (player + 1) + "'s turn =-=-\n");
            System.out.println("Place pile card: " + placePile.toString());
            System.out.println("Your cards: " + hands[player].toString());
            System.out.print("What card would you like to play or would you like to draw? (1-" + hands[player].length() + "/'draw'): ");
            String cardToPlay = TextIO.getlnString().toLowerCase();

            // while (moveNotValid)
            // ask for card
            // check if pick is in 1..numCardsLeft
            // check if card can be played
            //
            // play card
            //
            // check if card can be played: function taking picked card and top of placepile
            boolean actionTaken = false;
            while (!actionTaken) {
                if (isInt(cardToPlay)) {
                    int cardInt = Integer.parseInt(cardToPlay);
                    if (cardInt < 1 || cardInt > hands[player].length()) {
                        System.out.print("That is an invalid number. Enter one between 1 and " + hands[player].length() + ": ");
                        cardInt = TextIO.getlnInt();
                    } else if (!canBePlayed(hands[player].getCard(cardInt))) {
                        System.out.print("That card can't be played. Enter another one: ");
                        cardToPlay = TextIO.getlnString();
                    }
                    placePile = hands[player].getCard(cardInt);
                    hands[player].removeCard(cardInt);
                    placePile.specialMove(deck, hands, player);
                    actionTaken = true;
                } else if (cardToPlay.equals("draw")) {
                    hands[player].addCard(deck.deal());
                    actionTaken = true;
                } else {
                    System.out.print("That isn't a valid value. Enter 'draw' or a number from 1 to " + hands[player].length() + ": ");
                    cardToPlay = TextIO.getlnString();
                }
            }
            if (placePile instanceof Skip || placePile instanceof Plus2) {
                skip = true;
            }
            if (placePile instanceof Switch) {
                rev = !rev;
            }
            player = nextPlayer(player, hands.length);
//        } else {
//            System.out.println("\n-=-= Player " + (player + 1) + " has been skipped =-=-\n");
//            skip = false;
//        }
    }

    public boolean playable() {
        return deck.cardsLeft() >= 0;
    }

    private boolean canBePlayed(Card toPlay) {
        return (toPlay.getId() == 4 || toPlay.getColor().equals(placePile.getColor()) || toPlay.getNum() == placePile.getNum());
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
            nextPlayer = (currPlayer + 1)%players;
        } else if (!rev && skip){
            nextPlayer = (currPlayer + 2)%players;
        } else if (rev && skip) {
            nextPlayer = (currPlayer - 2)%players;
        } else {
            nextPlayer = (currPlayer-1)%players;
        }
        return nextPlayer;
    }
}
