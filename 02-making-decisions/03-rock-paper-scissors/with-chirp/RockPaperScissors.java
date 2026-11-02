import static chirp.Chirp.*;

public class RockPaperScissors {

    private static final String[] MOVES = {"Rock", "Paper", "Scissors"};

    public static void main(String[] args) {
        start();
        title("Rock Paper Scissors");

        String user = readChoice("Your move:", MOVES);
        String computer = MOVES[random(0, 2)];

        String result = compare(user, computer);

        blank();
        info("You: " + user + "  |  Computer: " + computer);
        line();

        switch (result) {
            case "Win" -> success("You win!");
            case "Loss" -> error("You lose.");
            default -> info("It's a draw.");
        }
    }

    // Three-step decision: draw → exhaustive win check → everything else is a loss.
    private static String compare(String user, String computer) {
        if (user.equals(computer)) return "Draw";

        if ((user.equals("Rock") && computer.equals("Scissors"))
                || (user.equals("Paper") && computer.equals("Rock"))
                || (user.equals("Scissors") && computer.equals("Paper"))) {
            return "Win";
        }

        return "Loss";
    }
}
