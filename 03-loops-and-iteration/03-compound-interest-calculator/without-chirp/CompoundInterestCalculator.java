import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompoundInterestCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Compound Interest Calculator");

        double principal = readDoubleAtLeast(scanner, "Principal amount:", 0.01);
        double annualRate = readDoubleInRange(scanner, "Annual rate (%):", 0, 100);
        int years = readIntInRange(scanner, "Number of years:", 1, 5);

        double monthlyRate = annualRate / 1200.0;
        int months = years * 12;

        double balance = principal;
        List<String[]> rows = new ArrayList<>();
        for (int m = 1; m <= months; m++) {
            double interest = balance * monthlyRate;
            balance += interest;
            rows.add(new String[]{
                    String.valueOf(m),
                    formatRupees(interest),
                    formatRupees(balance)
            });
        }

        double totalInterest = balance - principal;

        System.out.println();
        printTable(rows, new String[]{"Month", "Interest", "Balance"});

        System.out.println();
        System.out.println("✓ Principal:      " + formatRupees(principal));
        System.out.println("✓ Total interest: " + formatRupees(totalInterest));
        System.out.println("✓ Final balance:  " + formatRupees(balance));

        scanner.close();
    }

    private static String formatRupees(double amount) {
        return "Rs." + String.format("%,.2f", amount);
    }

    private static void printTable(List<String[]> rows, String[] headers) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (row[i].length() > widths[i]) widths[i] = row[i].length();
            }
        }

        printRow(headers, widths);
        for (int w : widths) System.out.print("-".repeat(w + 2));
        System.out.println();
        for (String[] row : rows) printRow(row, widths);
    }

    private static void printRow(String[] cells, int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            System.out.printf("%-" + (widths[i] + 2) + "s", cells[i]);
        }
        System.out.println();
    }

    private static double readDoubleAtLeast(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= min) return value;
                System.out.println("Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    private static double readDoubleInRange(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= min && value <= max) return value;
                System.out.println("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
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
