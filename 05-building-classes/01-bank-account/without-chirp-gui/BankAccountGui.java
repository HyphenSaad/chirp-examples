import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BankAccountGui extends Application {

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

    private static final String SUCCESS_STYLE = "-fx-font-size: 14px; -fx-text-fill: #16a34a;";
    private static final String ERROR_STYLE = "-fx-font-size: 14px; -fx-text-fill: #dc2626;";
    private static final String MUTED_STYLE = "-fx-font-size: 14px; -fx-text-fill: #71717a;";

    @Override
    public void start(Stage stage) {
        BankAccount account = new BankAccount("Saad", 1000);

        Label titleLabel = new Label("Bank Account");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label ownerLabel = new Label(account.owner());
        ownerLabel.setStyle(MUTED_STYLE);

        Label balanceCaption = new Label("Current balance");
        balanceCaption.setStyle(MUTED_STYLE);

        Label balanceLabel = new Label(formatRupees(account.balance()));
        balanceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #09090b;");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setPrefWidth(140);

        Button depositBtn = new Button("Deposit");
        depositBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white;");

        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #d4d4d8; -fx-border-radius: 6;");

        HBox actions = new HBox(8, amountField, depositBtn, withdrawBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label("Opened account for " + account.owner());
        statusLabel.setStyle(MUTED_STYLE);

        Label historyCaption = new Label("Transaction history");
        historyCaption.setStyle(MUTED_STYLE);

        ListView<String> historyView = new ListView<>(FXCollections.observableArrayList(account.transactions()));
        historyView.setPrefHeight(180);

        Runnable refresh = () -> {
            balanceLabel.setText(formatRupees(account.balance()));
            historyView.setItems(FXCollections.observableArrayList(account.transactions()));
        };

        depositBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (account.deposit(amount)) {
                    statusLabel.setText("Deposited " + formatRupees(amount));
                    statusLabel.setStyle(SUCCESS_STYLE);
                    amountField.clear();
                    refresh.run();
                } else {
                    statusLabel.setText("Amount must be greater than zero");
                    statusLabel.setStyle(ERROR_STYLE);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a number");
                statusLabel.setStyle(ERROR_STYLE);
            }
        });

        withdrawBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (account.withdraw(amount)) {
                    statusLabel.setText("Withdrew " + formatRupees(amount));
                    statusLabel.setStyle(SUCCESS_STYLE);
                    amountField.clear();
                    refresh.run();
                } else {
                    statusLabel.setText("Insufficient funds");
                    statusLabel.setStyle(ERROR_STYLE);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a number");
                statusLabel.setStyle(ERROR_STYLE);
            }
        });

        amountField.setOnAction(e -> depositBtn.fire());

        VBox root = new VBox(16, titleLabel, ownerLabel, balanceCaption, balanceLabel,
                actions, statusLabel, historyCaption, historyView);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 500, 560);
        stage.setTitle("Bank Account");
        stage.setScene(scene);
        stage.show();
    }

    private static String formatRupees(double amount) {
        return "Rs." + String.format("%,.2f", amount);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
