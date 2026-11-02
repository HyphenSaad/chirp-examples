import java.util.Scanner;

public class HelloGreeter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Hello Greeter");

        String name = readNonBlankLine(scanner, "What is your name?");
        int age = readIntInRange(scanner, "How old are you?", 1, 130);

        System.out.println();
        System.out.println("✓ Hello, " + name + "! You are " + age + " years old.");

        if (age >= 18) {
            System.out.println("ℹ You're an adult.");
        } else {
            System.out.println("ℹ You're a minor.");
        }

        System.out.println("=".repeat(40));
        System.out.println("✓ Goodbye, " + name + "!");

        scanner.close();
    }

    private static String readNonBlankLine(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("This field cannot be empty.");
        }
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
