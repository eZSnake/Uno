public class BasicBot {
    public BasicBot() {}

    public String playCard(Hand hand, Card placePile) {
        for (int i = 0; i < hand.length(); i++) {
            if (hand.getCard(i).isPlayable(placePile)) {
                return "" + (i+1);
            }
        }
        return "d";
    }

    public String chooseColor(Hand hand) {
//        for (int i = 0; i < hand.length(); i++) {
//            if (hand.getCard(i) instanceof NumCol) {
//                return hand.getCard(i).getColor();
//            }
//        }
//        int randCol = (int)(4*Math.random());
//        switch (randCol) {
//            case 0:
//                return "Blue";
//            case 1:
//                return "Green";
//            case 2:
//                return "Red";
//            default:
//                return "Yellow";
//        }
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
