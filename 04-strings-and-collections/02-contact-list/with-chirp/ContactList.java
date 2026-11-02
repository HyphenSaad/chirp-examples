import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContactList {

    public static void main(String[] args) {
        start();
        title("Contact List");

        List<String[]> contacts = new ArrayList<>();
        while (true) {
            String name = readLine("Name (blank to finish)");
            if (name.isEmpty()) break;
            String phone = readNonEmpty("Phone");
            contacts.add(new String[]{name, phone});
            blank();
        }

        if (contacts.isEmpty()) {
            info("No contacts.");
            return;
        }

        contacts.sort(Comparator.comparing(row -> row[0].toLowerCase()));

        blank();
        info("All contacts (" + contacts.size() + "):");
        blank();
        table(contacts, "Name", "Phone");

        blank();
        String query = readLine("Search query");
        List<String[]> matches = filter(contacts, row -> contains(row[0], query));
        printResults("Matches", matches);

        blank();
        String prefix = readLine("Prefix");
        List<String[]> byPrefix = filter(contacts, row -> startsWith(row[0], prefix));
        printResults("Matches", byPrefix);
    }

    private static void printResults(String label, List<String[]> rows) {
        blank();
        if (rows.isEmpty()) {
            info("No matches.");
            return;
        }
        info(label + " (" + rows.size() + "):");
        blank();
        table(rows, "Name", "Phone");
    }
}
