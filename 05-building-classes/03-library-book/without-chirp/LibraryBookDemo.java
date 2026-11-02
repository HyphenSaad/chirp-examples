import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class LibraryBookDemo {

    static class Book {
        private final String title;
        private final String author;
        private final String isbn;
        private String checkedOutBy;
        private LocalDate dueDate;

        Book(String title, String author, String isbn) {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (author == null || author.isBlank()) {
                throw new IllegalArgumentException("Author is required");
            }
            if (isbn == null || isbn.isBlank()) {
                throw new IllegalArgumentException("ISBN is required");
            }
            this.title = title;
            this.author = author;
            this.isbn = isbn;
        }

        String title() { return title; }
        String author() { return author; }
        String isbn() { return isbn; }
        String checkedOutBy() { return checkedOutBy; }
        LocalDate dueDate() { return dueDate; }

        boolean isAvailable() { return checkedOutBy == null; }

        boolean checkout(String member, LocalDate due) {
            if (!isAvailable()) return false;
            if (member == null || member.isBlank()) return false;
            if (due == null || due.isBefore(LocalDate.now())) return false;
            this.checkedOutBy = member;
            this.dueDate = due;
            return true;
        }

        boolean returnBook() {
            if (isAvailable()) return false;
            this.checkedOutBy = null;
            this.dueDate = null;
            return true;
        }

        long daysOverdue(LocalDate today) {
            if (isAvailable() || dueDate == null) return 0;
            long days = ChronoUnit.DAYS.between(dueDate, today);
            return Math.max(0, days);
        }

        double fineInRupees(LocalDate today) {
            return daysOverdue(today) * 10.0;
        }

        @Override
        public String toString() {
            return title + " by " + author + " (" + isbn + ")";
        }
    }

    private static final String[] ACTIONS = {
            "Checkout", "Return", "View status", "Query overdue", "Exit"
    };
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Library Book");

        Book book = new Book("Clean Code", "Robert C. Martin", "978-0132350884");
        System.out.println("ℹ " + book.toString());
        System.out.println();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Checkout" -> {
                    String member = readNonEmptyLine(scanner, "Member name:");
                    LocalDate due = readDate(scanner, "Due date (yyyy-mm-dd):");
                    if (book.checkout(member, due)) {
                        System.out.println("✓ Checked out to " + member + " — due " + due);
                    } else {
                        System.out.println("✗ Already checked out.");
                    }
                }
                case "Return" -> {
                    if (book.returnBook()) {
                        System.out.println("✓ Book returned.");
                    } else {
                        System.out.println("✗ Book is not checked out.");
                    }
                }
                case "View status" -> {
                    if (book.isAvailable()) {
                        System.out.println("ℹ Available");
                    } else {
                        System.out.println("ℹ Checked out by " + book.checkedOutBy()
                                + " (due " + book.dueDate() + ")");
                    }
                    System.out.println("ℹ " + book.toString());
                }
                case "Query overdue" -> {
                    LocalDate today = readDate(scanner, "Today's date (yyyy-mm-dd):");
                    long days = book.daysOverdue(today);
                    if (days == 0) {
                        System.out.println("ℹ Not overdue.");
                    } else {
                        System.out.println("⚠ Overdue by " + days + " days — fine "
                                + formatRupees(book.fineInRupees(today)));
                    }
                }
                case "Exit" -> {
                    System.out.println("ℹ Goodbye.");
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

    private static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a date in yyyy-mm-dd format.");
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
