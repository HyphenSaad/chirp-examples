# Expense Tracker

Append-only log of expenses. Add entries, view recent, and compute monthly summaries.

## Requirements

1. **File:** `expenses.log` in the current working directory, one expense per line:
   ```
   yyyy-mm-dd|category|amount|note
   ```
2. **First run** (file doesn't exist or is empty):
   - Seed with these five entries, using dates relative to today so the "recent" and "monthly" views have data:
     ```
     {today}          | Food      | 450   | Lunch at cafe
     {today}          | Transport | 150   | Auto to uni
     {today-1 day}    | Food      | 350   | Dinner
     {today-1 day}    | Books     | 800   | OOP textbook
     {today-2 days}   | Food      | 250   | Breakfast
     ```
   - Print `"ℹ Created expenses.log with sample data."`
3. **Subsequent runs** (file exists):
   - Do **not** re-seed
   - Print `"ℹ Loaded N entries from expenses.log."`
4. **Menu:**
   ```
   Action:
     1) Add expense
     2) View recent (last 10)
     3) Monthly summary
     4) Search by category
     5) Exit
   ```
5. **Add expense** — prompt for category (non-blank), amount (> 0), note (may be blank). Append a line to the file **immediately**, print `"✓ Expense added."`
6. **View recent** — read the file, print the last 10 entries in a table with columns `Date`, `Category`, `Amount`, `Note`. If fewer than 10 entries exist, print all.
7. **Monthly summary** — group entries by `yyyy-mm` and print a table with columns `Month`, `Entries`, `Total`.
8. **Search by category** — prompt for a category (case-insensitive), print all matching entries in the same table format as "View recent", plus a line `"ℹ Total: Rs.X"` summing the matching amounts.
9. **No data** — if the file has no entries, print `"ℹ No expenses recorded."` for any report.
10. **Exit** — print `"ℹ Goodbye."` and exit.

## Example interaction

```
========================================
│ Expense Tracker
========================================
ℹ Created expenses.log with sample data.

Action:
  1) Add expense
  2) View recent (last 10)
  3) Monthly summary
  4) Search by category
  5) Exit
Your choice [1-5]: 2

Date        Category   Amount  Note
----------  ---------  ------  ---------------
2026-11-01  Food       450.00  Lunch at cafe
2026-11-01  Transport  150.00  Auto to uni
2026-10-31  Food       350.00  Dinner
2026-10-31  Books      800.00  OOP textbook
2026-10-30  Food       250.00  Breakfast
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| First run | Seed 5 entries, save, print "Created" message |
| Second run | Load existing entries, do **not** re-seed |
| File exists but is empty | Treated as first run |
| Add an expense | New line appended — previous lines untouched |
| Add an expense, then restart | Entry still present |
| Recent when 3 entries exist | Print all 3 (not 10) |
| Recent when 15 entries exist | Print the **last** 10 (most recent), not the first 10 |
| Monthly summary across two months | One row per month, sorted by month ascending |
| Monthly summary with single month | One row |
| Search by category `"food"` (lowercase) | Matches `Food` — case-insensitive |
| Search by category `"xyz"` | Print `"ℹ No matches."` |
| Manually corrupt one line (missing `|`) | Line is skipped — other lines still load |
| Manually add a valid line | It appears in the next report |
| Note containing `|` | Preserved — the note field is the remainder of the line after the third `|` |

## The "append-only" pattern

This problem is the only one in the level that does **not** rewrite the entire file on every change. Every other example uses "load whole file → mutate list → write whole file". This one uses:

```java
TextFileIO.appendLine(FILE, line);
```

That's it — one call, one line, no reading and rewriting. Three reasons append-only is the right pattern for logs:

1. **O(1) per add** instead of O(n). Adding one expense to a 10,000-line log is one write, not 10,000.
2. **Crash-safe.** If the program crashes mid-write, only the last line is at risk. A whole-file rewrite that crashes halfway leaves the file corrupt.
3. **Auditable.** Every entry has a timestamp in the file — you can see *when* things were added. A rewritten file loses that history.

The trade-off: **you can't edit or delete entries**. Append-only is a "write once, read many" pattern. That's fine for financial transactions and logs — a real expense tracker would *never* let you delete a past expense, only add corrections. That's a design decision baked into the file format itself.

## The "last 10" — reverse vs forward reading

When there are 15 entries and the user asks for "recent", they want the **last** 10 (entries 6–15), not the first 10 (entries 1–10). In an append-only file, the most recent entries are at the *end*.

`TextFileIO.tail(FILE, 10)` returns the last 10 lines, in their original order. If you wanted "newest first" (reverse chronological), you'd need to reverse the list after reading:

```java
List<String> recent = TextFileIO.tail(FILE, 10);
java.util.Collections.reverse(recent);  // newest first
```

The demo keeps original order — chronological ascending — because that matches how the file itself is structured. If you wanted a "latest 10, newest first" view, reverse after reading.

## The "monthly summary" grouping pattern

```java
Map<String, List<Double>> byMonth = new LinkedHashMap<>();
for (String line : lines) {
    String[] parts = line.split("\\|", 4);
    String month = parts[0].substring(0, 7);  // "yyyy-mm"
    double amount = Double.parseDouble(parts[2]);
    byMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(amount);
}
```

Same `computeIfAbsent + ArrayList` pattern as Level 4's grade book. The "month" key is extracted as the first seven characters of the date string — `2026-11-01` becomes `2026-11`.

If you want `"November 2026"` instead of `"2026-11"`, use `DateTime.parseDate(parts[0])` and `DateTime.format(date, "MMMM yyyy")` — the demo keeps the ISO format for sortability.

## GUI extension

Build a JavaFX window that:
- Shows a `TableView` with columns Date, Category, Amount, Note
- Has an inline "Add" form at the top: category `TextField`, amount `TextField`, note `TextField`, Add button
- Has a category filter dropdown above the table
- Shows the monthly summary as a `PieChart` or `BarChart` (category breakdown) below the table

Same log file, same model, different driver.

## What you'll practice

- Append-only persistence — one-line writes, no rewriting
- Parsing delimited lines — `split("\\|", 4)` and why the limit matters
- Reading the "last N" of a file — the tail vs head distinction
- Aggregating a log — grouping entries by derived key (month) and summing
- Case-insensitive search — lowercase-both-sides comparison

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 140 | 262 |
| Add expense | `TextFileIO.appendLine(FILE, line)` | `Files.writeString(FILE, line+"\n", CREATE, APPEND)` in `try`/`catch` |
| Read all | `TextFileIO.readLines(FILE)` | `Files.readAllLines(FILE)` in `try`/`catch` |
| Recent (last 10) | `TextFileIO.tail(FILE, 10)` | manual `list.subList(size-10, size)` |
| Search by category | manual filter over `readLines()` | manual filter over `readAllLines()` |
| IOException handling | None — helpers return safe defaults | 2 `try`/`catch` blocks for read and append |
| Currency format | `rupees(amount)` | `"Rs." + String.format("%,.2f", amount)` |

The savings is the smallest of the three problems — the file operations themselves are simpler for append-only than for read-modify-write. But the tail helper, the safe defaults, and the consistent `rupees` output still add up to ~45 lines.
