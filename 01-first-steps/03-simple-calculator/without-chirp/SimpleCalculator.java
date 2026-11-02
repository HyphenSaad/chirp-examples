import java.util.Scanner;

public class SimpleCalculator {

    private static final String[] OPERATORS = {"+", "-", "*", "/"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Simple Calculator");

        double a = readDouble(scanner, "First number:");
        int opIndex = readMenuChoice(scanner, "Operator:", OPERATORS);
        String op = OPERATORS[opIndex];
        double b = readDouble(scanner, "Second number:");

        if (op.equals("/") && b == 0) {
            System.out.println();
            System.out.println("✗ Cannot divide by zero.");
            scanner.close();
            return;
        }

        double result = switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> Double.NaN;
        };

        System.out.println();
        System.out.println("✓ " + String.format("%.2f", a) + " " + op + " "
                + String.format("%.2f", b) + " = "
                + String.format("%.2f", result));

        scanner.close();
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
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
