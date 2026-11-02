import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        printTitle("Number Guessing Game");
        System.out.println("ℹ I'm thinking of a number between 1 and 100.");
        System.out.println();

        int target = random.nextInt(100) + 1;
        int attempts = 0;

        while (true) {
            int guess = readIntInRange(scanner, "Your guess:", 1, 100);
            attempts++;

            if (guess == target) {
                System.out.println();
                System.out.println("✓ Correct! You guessed it in " + attempts
                        + (attempts == 1 ? " attempt." : " attempts."));
                break;
            } else if (guess < target) {
                System.out.println("⚠ Too low. Try higher.");
            } else {
                System.out.println("⚠ Too high. Try lower.");
            }
        }

        scanner.close();
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
