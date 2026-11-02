import java.util.Scanner;

public class MultiplicationTable {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Multiplication Table");

        int n = readIntInRange(scanner, "Table size:", 1, 12);
        System.out.println();

        System.out.print("    ");
        for (int c = 1; c <= n; c++) System.out.printf("%4d", c);
        System.out.println();

        System.out.print("    ");
        for (int c = 1; c <= n; c++) System.out.print("----");
        System.out.println();

        for (int r = 1; r <= n; r++) {
            System.out.printf("%4d", r);
            for (int c = 1; c <= n; c++) System.out.printf("%4d", r * c);
            System.out.println();
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
