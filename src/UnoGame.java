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
                //
                // check if player has a card they can play
                //
                if (!canPlayACard(player)) {
                    System.out.println("You do not have a card to play. A card will be automatically drawn for you");
                    // TODO: handle case where there is no card left to draw !
                    hands[player].addCard(deck.deal());
                    player = nextPlayer(player, hands.length);
                    continue; // restart the round with the next player
                }
                System.out.print("Place pile card:\n" 
                + placePile.toString()
                + "\n\nYour cards:\n" 
                + hands[player].toString(placePile)
                + "\n\nWhat card would you like to play or would you like to draw? (1-" 
                + hands[player].length() + "/'draw','d'): ");
                
                boolean validPick = false;
                String cardToPlay = "";
                
                while (!validPick) {
                    cardToPlay = TextIO.getlnString().toLowerCase();
                    // now let's check if this was a valid pick - otherwise ask to choose again
                    // criteria to check:
                    // is numeric or string 'd, draw'
                    // is 1..handlength
                    validPick = isValidPick(cardToPlay, hands[player].length());
                }
                
                if (cardToPlay.equals("draw") || cardToPlay.equals("d")) {
                    hands[player].addCard(deck.deal());
                    continue;
                }
                
                Integer cardInt = Integer.parseInt(cardToPlay)-1;
                
                // play selected card onto placepile
                placePile = hands[player].getCard(cardInt);
                
                // remove selected card from player's hand
                hands[player].removeCard(cardInt);
                if (hands[player].length() <= 0) {
                    break;
                }
                
                // prepare next round by evaluating new placecard
                placePile.specialMove(deck, hands, player, "");
                
                // if switch then reverse direction
                if (placePile instanceof Switch) {
                    if (!((Switch) placePile).getHasSwitched()) {
                        rev = !rev;
                        ((Switch) placePile).setHasSwitched(true);
                    }
                }
                
                // handle skip card
                if (placePile instanceof Skip) {
                    // make sure the skip card is not open from the previous play
                    if (!((Skip) placePile).getHasSkipped()) {
                        skip = true;
                        ((Skip) placePile).setHasSkipped(true);
                    }
                } 
                
                if (placePile instanceof Plus2) {
                    // make sure the +2 card is not open from the previous play
                    
                    if (!((Plus2) placePile).getHasSkipped()) {
                        skip = true;
                        ((Plus2) placePile).setHasSkipped(true);
                    }
                }
                
                if (placePile instanceof Plus4) {
                    // make sure the +4 card is not open from the previous play
                    
                    if (!((Plus4) placePile).getHasSkipped()) {
                        skip = true;
                        ((Plus4) placePile).setHasSkipped(true);
                    }
                }
                
                
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
            for (int i = 0; i < hands.length; i++) {
                if (hands[i].length() == 0) {
                    return "Player " + (i+1) + " has won the game!\nCongratulations!";
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
        
        private boolean isValidPick(String cardToPlay, int cardsOnHand) {
            if (cardToPlay.equals("draw") || cardToPlay.equals("d")) {
                return true;
            }
            if ((!isInt(cardToPlay)) 
                || (Integer.parseInt(cardToPlay) < 1) 
                    || (Integer.parseInt(cardToPlay) > cardsOnHand)
            ) {
                System.out.print("Invalid pick. Enter a number between 1 and " 
                + cardsOnHand + " one or 'draw'/'d': ");
                return false;
            }
            return true;
        }
    }
    