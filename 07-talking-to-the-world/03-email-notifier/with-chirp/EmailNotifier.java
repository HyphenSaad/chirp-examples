import static chirp.Chirp.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import chirp.io.CsvFile;
import chirp.mail.EmailResult;

public class EmailNotifier {

    private static final String FILE = "recipients.csv";
    private static final List<String> HEADERS = List.of("name", "email");
    private static final String FROM = "notifier@chirp.local";

    public static void main(String[] args) {
        start();
        title("Email Notifier");

        List<Map<String, String>> recipients = CsvFile.readAsRecords(FILE);
        if (recipients.isEmpty()) {
            seedDemoRecipients();
            recipients = CsvFile.readAsRecords(FILE);
            info("Created " + FILE + " with sample data.");
        } else {
            info("Loaded " + recipients.size() + " recipient(s) from " + FILE + ".");
        }
        blank();

        String today = LocalDate.now().toString();
        String subject = "Course update — " + today;

        int sent = 0;
        for (Map<String, String> recipient : recipients) {
            String name = recipient.get("name");
            String email = recipient.get("email");

            info("Building message for " + name + " <" + email + ">");

            try {
                String body = fmt("Dear {name},\n\n"
                        + "This is an automated message from the Chirp course notifier.\n"
                        + "Please review the latest assignments before the deadline.\n\n"
                        + "Regards,\nThe Course Team",
                        "name", name);

                EmailResult result = email()
                        .host("smtp.example.com")
                        .from(FROM)
                        .to(email)
                        .subject(subject)
                        .text(body)
                        .dryRun(true)
                        .noLoader()
                        .send();

                if (result.isSuccess()) {
                    sent++;
                    success("Built OK — " + result.rawMessage().length() + " bytes of MIME");
                } else {
                    error("Skipped " + name + " — " + result.message());
                }
            } catch (RuntimeException e) {
                error("Skipped " + name + " — " + e.getMessage());
            }
            blank();
        }

        info("Sent: " + sent + " of " + recipients.size());
    }

    private static void seedDemoRecipients() {
        CsvFile.writeRecords(FILE, List.of(
                Map.of("name", "Saad Mansoor", "email", "saad@example.com"),
                Map.of("name", "Alice Khan", "email", "alice@example.com"),
                Map.of("name", "Bob Rahim", "email", "bob@example.com")
        ), HEADERS);
    }
}
