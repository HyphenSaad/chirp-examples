import java.util.Random;
import java.util.Scanner;

public class RockPaperScissors {

    private static final String[] MOVES = {"Rock", "Paper", "Scissors"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        printTitle("Rock Paper Scissors");

        int userIndex = readMenuChoice(scanner, "Your move:", MOVES);
        String user = MOVES[userIndex];
        String computer = MOVES[random.nextInt(MOVES.length)];

        String result = compare(user, computer);

        System.out.println();
        System.out.println("ℹ You: " + user + "  |  Computer: " + computer);
        System.out.println("=".repeat(40));

        switch (result) {
            case "Win" -> System.out.println("✓ You win!");
            case "Loss" -> System.out.println("✗ You lose.");
            default -> System.out.println("ℹ It's a draw.");
        }

        scanner.close();
    }

    private static String compare(String user, String computer) {
        if (user.equals(computer)) return "Draw";

        if ((user.equals("Rock") && computer.equals("Scissors"))
                || (user.equals("Paper") && computer.equals("Rock"))
                || (user.equals("Scissors") && computer.equals("Paper"))) {
            return "Win";
        }

        return "Loss";
    }

    private static int readMenuChoice(Scanner scanner, String title, String[] options) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ") " + options[i]);
            }
            System.out.print("Your choice [1-" + options.length + "]: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.length) return choice - 1;
                System.out.println("Please enter a number between 1 and " + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
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
