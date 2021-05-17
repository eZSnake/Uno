import java.util.ArrayList;

public class BasicBot {
    public BasicBot() {}

    public Card playCard(Hand hand, Card placePile) {
        ArrayList<Card> playableCards = new ArrayList<>();
        for (int i = 0; i < hand.length(); i++) {
//            System.out.println(hand.getCard((i)));
            if (hand.getCard(i).isPlayable(placePile)) {
                playableCards.add(hand.getCard(i));
            }
        }
        //Uncomment to see the cards the bot chooses from
//        System.out.println(playableCards);
        return playableCards.get((int)(playableCards.size() * Math.random()));
    }

    public String chooseColor(Hand hand) {
        int blueCards = 0;
        int greenCards = 0;
        int redCards = 0;
        int yellowCards = 0;
        for (int i = 0; i < hand.length(); i++) {
            String col = hand.getCard(i).getColor();
            switch (col) {
                case "Blue":
                    blueCards++;
                case "Green":
                    greenCards++;
                case "Red":
                    redCards++;
                default:
                    yellowCards++;
            }
        }
        if (blueCards >= greenCards && blueCards >= redCards && blueCards >= yellowCards) {
            return "Blue";
        } else if (greenCards >= blueCards && greenCards >= redCards && greenCards >= yellowCards) {
            return "Red";
        } else if (redCards >= blueCards && redCards >= greenCards && redCards >= yellowCards) {
            return "Red";
        } else {
            return "Yellow";
        }
    }
}
