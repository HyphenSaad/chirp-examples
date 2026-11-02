import static chirp.Chirp.*;

public class BankAccountDemo {

    static class BankAccount {
        private final String owner;
        private double balance;
        private final java.util.List<String> transactions = new java.util.ArrayList<>();

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
        java.util.List<String> transactions() { return java.util.List.copyOf(transactions); }

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

    public static void main(String[] args) {
        start();
        title("Bank Account Demo");

        BankAccount account = new BankAccount("Saad", 1000);
        info("Opened account for " + account.owner() + " with " + rupees(account.balance()));
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Deposit", "Withdraw", "View balance", "View history", "Exit");

            switch (action) {
                case "Deposit" -> {
                    double amount = readDouble("Amount").min(0.01).read();
                    account.deposit(amount);
                    success("Deposited " + rupees(amount) + " — balance " + rupees(account.balance()));
                }
                case "Withdraw" -> {
                    double amount = readDouble("Amount").min(0.01).read();
                    if (account.withdraw(amount)) {
                        success("Withdrew " + rupees(amount) + " — balance " + rupees(account.balance()));
                    } else {
                        error("Insufficient funds — balance is " + rupees(account.balance()));
                    }
                }
                case "View balance" -> info("Current balance: " + rupees(account.balance()));
                case "View history" -> {
                    blank();
                    for (String line : account.transactions()) info(line);
                }
                case "Exit" -> {
                    info("Goodbye, " + account.owner() + ".");
                    return;
                }
            }
            blank();
        }
    }
}
