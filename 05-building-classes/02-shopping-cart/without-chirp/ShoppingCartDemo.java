import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShoppingCartDemo {

    record Item(String name, double price, int quantity) {
        double lineTotal() { return price * quantity; }
    }

    static class ShoppingCart {
        private final List<Item> items = new ArrayList<>();
        private double discountPercent = 0;

        void addItem(String name, double price, int quantity) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Item name is required");
            }
            if (price <= 0) {
                throw new IllegalArgumentException("Price must be > 0");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0");
            }

            for (int i = 0; i < items.size(); i++) {
                Item existing = items.get(i);
                if (existing.name().equals(name)) {
                    items.set(i, new Item(name, price, existing.quantity() + quantity));
                    return;
                }
            }
            items.add(new Item(name, price, quantity));
        }

        boolean removeItem(String name) {
            return items.removeIf(item -> item.name().equals(name));
        }

        void applyDiscount(double percent) {
            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            this.discountPercent = percent;
        }

        List<Item> items() { return List.copyOf(items); }

        double subtotal() {
            double total = 0;
            for (Item item : items) total += item.lineTotal();
            return total;
        }

        double discountAmount() { return subtotal() * discountPercent / 100; }
        double total() { return subtotal() - discountAmount(); }
        double discountPercent() { return discountPercent; }

        int itemCount() {
            int count = 0;
            for (Item item : items) count += item.quantity();
            return count;
        }
    }

    private static final String[] ACTIONS = {
            "Add item", "Remove item", "Apply discount", "View cart", "Checkout"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Shopping Cart");
        System.out.println();

        ShoppingCart cart = new ShoppingCart();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Add item" -> {
                    String name = readNonEmptyLine(scanner, "Item name:");
                    double price = readPositiveDouble(scanner, "Price:");
                    int qty = readPositiveInt(scanner, "Quantity:");
                    cart.addItem(name, price, qty);
                    System.out.println("✓ Added " + qty + " × " + name + " @ " + formatRupees(price));
                }
                case "Remove item" -> {
                    String name = readNonEmptyLine(scanner, "Item name:");
                    if (cart.removeItem(name)) {
                        System.out.println("✓ Removed " + name + " from cart.");
                    } else {
                        System.out.println("✗ No such item in cart.");
                    }
                }
                case "Apply discount" -> {
                    double percent = readDoubleInRange(scanner, "Percent:", 0, 100);
                    cart.applyDiscount(percent);
                    System.out.println("✓ Discount set to "
                            + String.format("%.0f", percent) + "%");
                }
                case "View cart" -> {
                    if (cart.items().isEmpty()) {
                        System.out.println("ℹ Cart is empty.");
                    } else {
                        printCart(cart);
                    }
                }
                case "Checkout" -> {
                    if (cart.items().isEmpty()) {
                        System.out.println("ℹ Nothing to check out.");
                    } else {
                        printCart(cart);
                        System.out.println("✓ Checked out — thank you!");
                        scanner.close();
                        return;
                    }
                }
            }
            System.out.println();
        }
    }

    private static void printCart(ShoppingCart cart) {
        List<String[]> rows = new ArrayList<>();
        for (Item item : cart.items()) {
            rows.add(new String[]{
                    item.name(),
                    formatRupees(item.price()),
                    String.valueOf(item.quantity()),
                    formatRupees(item.lineTotal())
            });
        }

        System.out.println();
        printTable(rows, new String[]{"Item", "Price", "Qty", "Total"});
        System.out.println();
        System.out.println("ℹ Subtotal:  " + formatRupees(cart.subtotal()));
        System.out.println("ℹ Discount:  " + formatRupees(cart.discountAmount())
                + "  (" + String.format("%.0f", cart.discountPercent()) + "%)");
        System.out.println("ℹ Total:     " + formatRupees(cart.total()));
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

    private static String readNonEmptyLine(Scanner scanner, String prompt) {
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

    private static int readPositiveInt(Scanner scanner, String prompt) {
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
                if (value > 0) return value;
                System.out.println("Value must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static void printTable(List<String[]> rows, String[] headers) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
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

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
