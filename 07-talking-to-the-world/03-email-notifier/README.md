# Email Notifier

Read a CSV of recipients, build a personalised email for each, and send it in **dry-run mode** — the full MIME message is constructed but not transmitted. Log the results.

## Requirements

1. **File:** `recipients.csv` in the current working directory, with header row:
   ```
   name,email
   ```
2. **First run** (file doesn't exist or is empty):
   - Seed with these three recipients:
     - Saad Mansoor / saad@example.com
     - Alice Khan / alice@example.com
     - Bob Rahim / bob@example.com
   - Print `"ℹ Created recipients.csv with sample data."`
3. **Subsequent runs** (file exists):
   - Print `"ℹ Loaded N recipient(s) from recipients.csv."`
4. For each recipient, build an email with:
   - **From:** `notifier@chirp.local`
   - **Subject:** `"Course update — {date}"` where `{date}` is today in `yyyy-mm-dd` format
   - **Body:**
     ```
     Dear {name},

     This is an automated message from the Chirp course notifier.
     Please review the latest assignments before the deadline.

     Regards,
     The Course Team
     ```
5. **Send each message in dry-run mode** — do not attempt a network connection. Use `Email.builder().dryRun(true).send()`.
6. For each recipient, print:
   ```
   ℹ Building message for {name} <{email}>
   ✓ Built OK — NNN bytes of MIME
   ```
   If validation fails for a recipient (e.g., invalid email format), print:
   ```
   ✗ Skipped {name} — {reason}
   ```
   and continue with the next recipient.
7. After all recipients, print a summary:
   ```
   ℹ Sent: X of Y
   ```
8. Press-Enter-to-exit at the end.

## Example interaction

```
========================================
│ Email Notifier
========================================
ℹ Created recipients.csv with sample data.

ℹ Building message for Saad Mansoor <saad@example.com>
✓ Built OK — 412 bytes of MIME

ℹ Building message for Alice Khan <alice@example.com>
✓ Built OK — 398 bytes of MIME

ℹ Building message for Bob Rahim <bob@example.com>
✓ Built OK — 405 bytes of MIME

ℹ Sent: 3 of 3
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| First run | Seed 3 recipients, save, print "Created" message |
| Second run | Load existing recipients, do not re-seed |
| Empty file (or empty after header) | No recipients — print summary with 0 of 0, do not attempt any send |
| Recipient with invalid email (`"not-an-email"`) | Skipped with a message, other recipients continue |
| Recipient with blank name | Skipped with a message |
| Recipient with valid data | MIME message built, size reported |
| Invalid `from` address in source code | Every recipient is skipped — validation error caught at each attempt |
| File manually edited to add one recipient | Loaded on next run |
| File manually edited with `,,,,` header only | Header is present but no recipients — summary 0 of 0 |

## The dry-run lesson

`Email.builder().dryRun(true)` runs the **entire** email pipeline except the network send:

- Validates the request (from, to, subject, body all present and well-formed)
- Resolves the password strategy (env file, prompt, direct)
- Builds the complete MIME message — headers, boundaries, base64 attachments
- Reports the message bytes, recipients, and duration

What it does **not** do:

- Open an SMTP connection
- Authenticate
- Send the message

The trade-off: `dryRun` proves the code path is correct without needing SMTP credentials. It's a common pattern in production for testing mail code in staging environments where SMTP isn't available.

If you want to actually send email, replace `.dryRun(true)` with:

```java
.passwordFromEnv(".env", "SMTP_PASSWORD")
```

and set up an `.env` file with real credentials plus a real SMTP host. Everything else in the demo works unchanged. That's the point of separating the two — the demo runs without credentials, and the real version is a one-line change.

## The "build, then report" batch pattern

This is the same shape as Level 4's grade book, but the "build" step is now **email construction**:

```java
int sent = 0;
for (Map<String, String> recipient : recipients) {
    try {
        EmailResult result = buildAndSend(recipient);
        if (result.isSuccess()) {
            sent++;
            success("Built OK");
        } else {
            error("Failed: " + result.message());
        }
    } catch (RuntimeException e) {
        error("Skipped: " + e.getMessage());
    }
}
info("Sent: " + sent + " of " + recipients.size());
```

Three properties of this pattern:

1. **One bad recipient must not stop the batch.** Every operation is in its own try/catch (or returns a result object that carries the error). The loop continues.
2. **The count at the end is authoritative.** `sent` is incremented exactly once per successful send — no ambiguity.
3. **Failures are logged, not thrown.** A recipient with an invalid email is an *expected* condition, not an exceptional one. It gets an `error(...)` message and moves on.

That's the pattern every batch operation uses — emails, uploads, payments, migrations. One failure at a time, log and continue, count at the end.

## The "templated message" pattern

The message body is built from a template with one placeholder:

```java
String body = fmt("Dear {name},\n\n"
    + "This is an automated message from the Chirp course notifier.\n"
    + "Please review the latest assignments before the deadline.\n\n"
    + "Regards,\nThe Course Team",
    "name", recipient.get("name"));
```

The alternative is `String.format("Dear %s, ...", name)` — same output, slightly different mechanism. Chirp's `fmt` uses `{key}` placeholders, which are more readable when there are multiple substitution points.

The pattern becomes essential when you have more than one placeholder. `"Dear {name}, your code is {code} and expires on {date}"` is immediately clear; the `%s` version requires reading the argument order. Templates scale; positional formats don't.

## GUI extension

Build a JavaFX window that:
- Shows a `TableView` with columns `Name`, `Email`, `Status` (empty / built / skipped)
- Has a "Send all" button that processes each recipient and updates the status column
- Has a summary label at the bottom showing "N of M sent"
- Uses `Ui.runAsync(...)` so the button click doesn't freeze the window during processing

Same CSV, same email code, different driver — and this is the case where `Ui.runAsync` becomes essential rather than optional.

## What you'll practice

- Reading a CSV of contacts
- Composing an email with from, to, subject, body, date
- Building the MIME message without sending it
- Batch processing with per-item error handling
- Reporting a summary count at the end

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 75 | 137 |
| Read recipients | `CsvFile.readAsRecords(FILE)` | manual `Files.readAllLines` + `split(",")` + `LinkedHashMap` per row |
| Build & validate | `Email.builder().to(...).subject(...).text(...).dryRun(true).send()` | manual MIME construction (~40 lines): build `From:`, `To:`, `Subject:`, `Date:`, `MIME-Version:` headers, blank line, body |
| Validate recipient | Automatic — `email()` validators run inside `send()` | Manual — `Check.isEmail()` call for each field |
| Report result | `EmailResult.success()`, `EmailResult.message()`, `EmailResult.mime()` | Manual try/catch, manual status tracking |
| Currency/date format | `formatDate(today, "yyyy-MM-dd")` | `LocalDate.now().toString()` or `DateTimeFormatter` |

The MIME construction itself is where Chirp's email module earns its keep. Building a valid RFC 5322 message by hand — with correct `From:` format, `Date:` in RFC 1123 format, `MIME-Version: 1.0`, and proper CRLF line endings — is 30–50 lines that every version would otherwise duplicate.
