import chirp.ui.*;
import javafx.geometry.Pos;
import static chirp.Chirp.*;

public class BankAccountGui {

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
        Ui.app(() -> {
            BankAccount account = new BankAccount("Saad", 1000);

            Label balanceLabel = Label.heading(rupees(account.balance())).color("#09090b");
            Label statusLabel = Label.muted("Opened account for " + account.owner());
            ListView historyView = ListView.of();
            historyView.setItems(javafx.collections.FXCollections.observableArrayList(account.transactions()));

            TextField amountField = TextField.of().placeholder("Amount");

            Runnable refresh = () -> {
                balanceLabel.text(rupees(account.balance()));
                historyView.setItems(javafx.collections.FXCollections.observableArrayList(account.transactions()));
            };

            Runnable deposit = () -> {
                try {
                    double amount = Double.parseDouble(amountField.value().trim());
                    if (account.deposit(amount)) {
                        statusLabel.text("Deposited " + rupees(amount)).variant("success");
                        amountField.text("");
                        refresh.run();
                    } else {
                        statusLabel.text("Amount must be greater than zero").variant("error");
                    }
                } catch (NumberFormatException e) {
                    statusLabel.text("Enter a number").variant("error");
                }
            };

            Runnable withdraw = () -> {
                try {
                    double amount = Double.parseDouble(amountField.value().trim());
                    if (account.withdraw(amount)) {
                        statusLabel.text("Withdrew " + rupees(amount)).variant("success");
                        amountField.text("");
                        refresh.run();
                    } else {
                        statusLabel.text("Insufficient funds").variant("error");
                    }
                } catch (NumberFormatException e) {
                    statusLabel.text("Enter a number").variant("error");
                }
            };

            Button depositBtn = Button.primary("Deposit").onClick(() -> deposit.run());
            Button withdrawBtn = Button.outline("Withdraw").onClick(() -> withdraw.run());

            amountField.onEnter(() -> deposit.run());

            Row actions = Row.h(8).add(amountField, depositBtn, withdrawBtn).align(Pos.CENTER_LEFT);

            Stack root = Stack.v(16).padding(20);
            root.add(
                    Label.heading("Bank Account"),
                    Label.muted(account.owner()),
                    Label.muted("Current balance"),
                    balanceLabel,
                    actions,
                    statusLabel,
                    Label.muted("Transaction history"),
                    historyView.fullWidth());

            Ui.show("Bank Account", root);
        });
    }
}
