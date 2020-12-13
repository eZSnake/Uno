public class ChangeCol extends Card {
    public ChangeCol() {
        super("", 4, 10);
    }

    public String toString() {
        return getColor() + " Change Color";
    }

    public void specialMove(Deck deck, Hand[] hands, int currPlayer) {
        System.out.print("What would you like to change the color to? ");
        String col = TextIO.getlnString().toLowerCase();
        while (!col.equals("blue") && !col.equals("red") && !col.equals("green") && !col.equals("yellow")) {
            System.out.print("Invalid color. Choose between Blue, Red, Green, Yellow. ");
            col = TextIO.getlnString().toLowerCase();
        }
        this.setColor(col.substring(0, 1).toUpperCase() + col.substring(1));
    }
}
