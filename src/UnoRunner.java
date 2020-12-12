public class UnoRunner {
    public static void main(String[] args) {
        System.out.println("Welcome to the game of Uno.\nEach player starts with 7 cards and first with 0 left wins.\nThe same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\nIf you can't go, you draw a card.");
        System.out.print("How many players would you like (2-4)? ");
        int players = TextIO.getlnInt();
        while (players < 2 || players > 4) {
            System.out.print(players + " is an invalid amount of players. Enter a number between 2 and 4. ");
            players = TextIO.getlnInt();
        }
        UnoGame myGame = new UnoGame(players);
        while (myGame.playable()) {
            myGame.playRound(0);
        }
    }
}
