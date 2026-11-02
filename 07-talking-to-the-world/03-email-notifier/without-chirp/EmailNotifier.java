import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmailNotifier {

    private static final String FILE = "recipients.csv";
    private static final String HEADER = "name,email";
    private static final String FROM = "notifier@chirp.local";

    public static void main(String[] args) {
        printTitle("Email Notifier");

        List<Map<String, String>> recipients = loadRecipients();
        if (recipients.isEmpty()) {
            seedDemoRecipients();
            recipients = loadRecipients();
            System.out.println("ℹ Created " + FILE + " with sample data.");
        } else {
            System.out.println("ℹ Loaded " + recipients.size() + " recipient(s) from " + FILE + ".");
        }
        System.out.println();

        String today = LocalDate.now().toString();
        String subject = "Course update — " + today;

        int sent = 0;
        for (Map<String, String> recipient : recipients) {
            String name = recipient.get("name");
            String email = recipient.get("email");

            System.out.println("ℹ Building message for " + name + " <" + email + ">");

            if (!isValidEmail(email)) {
                System.out.println("✗ Skipped " + name + " — invalid email address");
                System.out.println();
                continue;
            }
            if (name == null || name.isBlank()) {
                System.out.println("✗ Skipped " + name + " — missing name");
                System.out.println();
                continue;
            }

            try {
                String body = "Dear " + name + ",\n\n"
                        + "This is an automated message from the Chirp course notifier.\n"
                        + "Please review the latest assignments before the deadline.\n\n"
                        + "Regards,\nThe Course Team";

                String mime = buildMime(FROM, email, subject, body);
                sent++;
                System.out.println("✓ Built OK — " + mime.getBytes().length + " bytes of MIME");
            } catch (RuntimeException e) {
                System.out.println("✗ Skipped " + name + " — " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("ℹ Sent: " + sent + " of " + recipients.size());
    }

    // Manual MIME construction — the minimum set of headers a mail server
    // would accept for a plain-text message. Real messages add charset,
    // encoding, and MIME boundaries.
    private static String buildMime(String from, String to, String subject, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("From: <").append(from).append(">\r\n");
        sb.append("To: <").append(to).append(">\r\n");
        sb.append("Subject: ").append(subject).append("\r\n");
        sb.append("Date: ")
                .append(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .append("\r\n");
        sb.append("MIME-Version: 1.0\r\n");
        sb.append("Content-Type: text/plain; charset=UTF-8\r\n");
        sb.append("\r\n");
        sb.append(body);
        return sb.toString();
    }

    private static boolean isValidEmail(String email) {
        return email != null
                && email.matches("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@"
                        + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                        + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");
    }

    private static List<Map<String, String>> loadRecipients() {
        List<Map<String, String>> recipients = new ArrayList<>();
        Path file = Path.of(FILE);
        if (!Files.exists(file)) return recipients;

        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 2) continue;
                Map<String, String> r = new LinkedHashMap<>();
                r.put("name", parts[0]);
                r.put("email", parts[1]);
                recipients.add(r);
            }
        } catch (IOException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
        }
        return recipients;
    }

    private static void seedDemoRecipients() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER).append('\n');
            sb.append("Saad Mansoor,saad@example.com\n");
            sb.append("Alice Khan,alice@example.com\n");
            sb.append("Bob Rahim,bob@example.com\n");
            Files.writeString(Path.of(FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("Cannot create " + FILE + ": " + e.getMessage());
        }
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
