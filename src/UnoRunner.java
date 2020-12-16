public class UnoRunner { //Eike Rehwald
    public static void main(String[] args) {
        System.out.println("Welcome to the game of Uno.\nEach player starts with 7 cards and first with 0 left wins.\nThe same color can go on the same color, " +
                "the same number can go on the same number, and wish cards can go on any card.\nIf you can't go, you draw a card.\n" +
                "To play a card, enter the corresponding number to the left of it or enter 'draw' or 'd' to draw a card.\n" +
                "If a card is playable, a '(*)' will show up in front of it.");
        System.out.print("Would you like to play against a bot? (y/n) ");
        boolean bot = TextIO.getlnBoolean();
        if (!bot) {
            System.out.print("How many players would you like (2-4)? ");
            int players = TextIO.getlnInt();
            while (players < 2 || players > 4) {
                System.out.print(players + " is an invalid amount of players. Enter a number between 2 and 4. ");
                players = TextIO.getlnInt();
            }
            UnoGame myGame = new UnoGame(players);
            myGame.playRounds(0);
        } else {
            BotUnoGame botGame = new BotUnoGame();
            botGame.playRounds(0);
        }
    }
}
