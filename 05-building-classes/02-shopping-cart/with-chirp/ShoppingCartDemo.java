import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.List;

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

    public static void main(String[] args) {
        start();
        title("Shopping Cart");

        ShoppingCart cart = new ShoppingCart();
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Add item", "Remove item", "Apply discount", "View cart", "Checkout");

            switch (action) {
                case "Add item" -> {
                    String name = readNonEmpty("Item name");
                    double price = readDouble("Price").min(0.01).read();
                    int qty = readInt("Quantity", 1, Integer.MAX_VALUE);
                    cart.addItem(name, price, qty);
                    success("Added " + qty + " × " + name + " @ " + rupees(price));
                }
                case "Remove item" -> {
                    String name = readNonEmpty("Item name");
                    if (cart.removeItem(name)) {
                        success("Removed " + name + " from cart.");
                    } else {
                        error("No such item in cart.");
                    }
                }
                case "Apply discount" -> {
                    double percent = readDouble("Percent").min(0).max(100).read();
                    cart.applyDiscount(percent);
                    success("Discount set to " + percent(percent, 0) + "%");
                }
                case "View cart" -> {
                    if (cart.items().isEmpty()) {
                        info("Cart is empty.");
                    } else {
                        printCart(cart);
                    }
                }
                case "Checkout" -> {
                    if (cart.items().isEmpty()) {
                        info("Nothing to check out.");
                    } else {
                        printCart(cart);
                        success("Checked out — thank you!");
                        return;
                    }
                }
            }
            blank();
        }
    }

    private static void printCart(ShoppingCart cart) {
        List<String[]> rows = new ArrayList<>();
        for (Item item : cart.items()) {
            rows.add(new String[]{
                    item.name(),
                    rupees(item.price()),
                    String.valueOf(item.quantity()),
                    rupees(item.lineTotal())
            });
        }

        blank();
        table(rows, "Item", "Price", "Qty", "Total");
        blank();
        info("Subtotal:  " + rupees(cart.subtotal()));
        info("Discount:  " + rupees(cart.discountAmount())
                + "  (" + percent(cart.discountPercent(), 0) + ")");
        info("Total:     " + rupees(cart.total()));
    }
}
