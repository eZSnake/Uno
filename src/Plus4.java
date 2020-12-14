public class Plus4 extends Card {
    public Plus4() {
        super("", 4, -5);
    }

    public String toString() {
        return getColor() + " +4 and Change Color";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
        System.out.print("What would you like to change the color to? ");
        String col = TextIO.getlnString().toLowerCase();
        while (!col.equals("blue") && !col.equals("red") && !col.equals("green") && !col.equals("yellow")) {
            System.out.print("Invalid color. Choose between Blue, Red, Green, Yellow. ");
            col = TextIO.getlnString().toLowerCase();
        }
        this.setColor(col.substring(0, 1).toUpperCase() + col.substring(1));
        if (currPlayer < hands.length-1) {
            currPlayer++;
        } else {
            currPlayer = 0;
        }
        for (int i = 0; i < 4; i++) {
            hands[currPlayer].addCard(deck.deal());
        }
    }
}
