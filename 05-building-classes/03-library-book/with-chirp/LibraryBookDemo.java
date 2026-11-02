import static chirp.Chirp.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    public static void main(String[] args) {
        start();
        title("Library Book");

        Book book = new Book("Clean Code", "Robert C. Martin", "978-0132350884");
        info(book.toString());
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Checkout", "Return", "View status", "Query overdue", "Exit");

            switch (action) {
                case "Checkout" -> {
                    String member = readNonEmpty("Member name");
                    LocalDate due = readDate("Due date (yyyy-mm-dd)");
                    if (book.checkout(member, due)) {
                        success("Checked out to " + member + " — due " + due);
                    } else {
                        error("Already checked out.");
                    }
                }
                case "Return" -> {
                    if (book.returnBook()) {
                        success("Book returned.");
                    } else {
                        error("Book is not checked out.");
                    }
                }
                case "View status" -> {
                    if (book.isAvailable()) {
                        info("Available");
                    } else {
                        info("Checked out by " + book.checkedOutBy()
                                + " (due " + book.dueDate() + ")");
                    }
                    info(book.toString());
                }
                case "Query overdue" -> {
                    LocalDate today = readDate("Today's date (yyyy-mm-dd)");
                    long days = book.daysOverdue(today);
                    if (days == 0) {
                        info("Not overdue.");
                    } else {
                        warn("Overdue by " + days + " days — fine "
                                + rupees(book.fineInRupees(today)));
                    }
                }
                case "Exit" -> {
                    info("Goodbye.");
                    return;
                }
            }
            blank();
        }
    }
}
