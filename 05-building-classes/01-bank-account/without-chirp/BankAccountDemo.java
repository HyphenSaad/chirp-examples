import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankAccountDemo {

    static class BankAccount {
        private final String owner;
        private double balance;
        private final List<String> transactions = new ArrayList<>();

        BankAccount(String owner, double initialBalance) {
            if (owner == null || owner.isBlank()) {
                throw new IllegalArgumentException("Owner name is required");
            }
            if (initialBalance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
            this.owner = owner;
            this.balance = initialBalance;
            transactions.add("Opened with " + formatMoney(initialBalance));
        }

        String owner() { return owner; }
        double balance() { return balance; }
        List<String> transactions() { return List.copyOf(transactions); }

        boolean deposit(double amount) {
            if (amount <= 0) return false;
            balance += amount;
            transactions.add("Deposit " + formatMoney(amount) + " — balance " + formatMoney(balance));
            return true;
        }

        boolean withdraw(double amount) {
            if (amount <= 0) return false;
            if (amount > balance) return false;
            balance -= amount;
            transactions.add("Withdraw " + formatMoney(amount) + " — balance " + formatMoney(balance));
            return true;
        }

        private static String formatMoney(double amount) {
            return "Rs." + String.format("%,.2f", amount);
        }
    }

    private static final String[] ACTIONS = {
            "Deposit", "Withdraw", "View balance", "View history", "Exit"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Bank Account Demo");

        BankAccount account = new BankAccount("Saad", 1000);
        System.out.println("ℹ Opened account for " + account.owner()
                + " with " + formatRupees(account.balance()));
        System.out.println();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Deposit" -> {
                    double amount = readPositiveDouble(scanner, "Amount:");
                    account.deposit(amount);
                    System.out.println("✓ Deposited " + formatRupees(amount)
                            + " — balance " + formatRupees(account.balance()));
                }
                case "Withdraw" -> {
                    double amount = readPositiveDouble(scanner, "Amount:");
                    if (account.withdraw(amount)) {
                        System.out.println("✓ Withdrew " + formatRupees(amount)
                                + " — balance " + formatRupees(account.balance()));
                    } else {
                        System.out.println("✗ Insufficient funds — balance is "
                                + formatRupees(account.balance()));
                    }
                }
                case "View balance" -> System.out.println("ℹ Current balance: "
                        + formatRupees(account.balance()));
                case "View history" -> {
                    System.out.println();
                    for (String line : account.transactions()) {
                        System.out.println("ℹ " + line);
                    }
                }
                case "Exit" -> {
                    System.out.println("ℹ Goodbye, " + account.owner() + ".");
                    scanner.close();
                    return;
                }
            }
            System.out.println();
        }
    }

    private static String formatRupees(double amount) {
        return "Rs." + String.format("%,.2f", amount);
    }

    private static String readMenuChoice(Scanner scanner, String title, String[] options) {
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
                if (choice >= 1 && choice <= options.length) return options[choice - 1];
                System.out.println("Please enter a number between 1 and " + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static double readPositiveDouble(Scanner scanner, String prompt) {
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
                if (value > 0) return value;
                System.out.println("Value must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
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
