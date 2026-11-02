import static chirp.Chirp.*;

public class NumberGuessingGame {

    public static void main(String[] args) {
        start();
        title("Number Guessing Game");

        int target = random(1, 100);
        info("I'm thinking of a number between 1 and 100.");
        blank();

        int attempts = 0;
        while (true) {
            int guess = readInt("Your guess", 1, 100);
            attempts++;

            if (guess == target) {
                blank();
                success("Correct! You guessed it in " + attempts
                        + (attempts == 1 ? " attempt." : " attempts."));
                return;
            } else if (guess < target) {
                warn("Too low. Try higher.");
            } else {
                warn("Too high. Try lower.");
            }
        }
    }
}
