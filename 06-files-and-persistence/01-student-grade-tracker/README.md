# Student Grade Tracker

Persist a list of student grades to a CSV file. Support adding grades and printing two aggregate reports.

## Requirements

1. **File:** `grades.csv` in the current working directory, with header row:
   ```
   name,subject,score
   ```
2. **First run** (file doesn't exist or is empty):
   - Seed the file with these five grades:
     - Saad / Math / 85
     - Saad / Physics / 78
     - Alice / Math / 92
     - Alice / Physics / 88
     - Bob / Math / 75
   - Print `"ℹ Created grades.csv with sample data."`
3. **Subsequent runs** (file exists with data):
   - Load the existing records
   - Print `"ℹ Loaded N grade(s) from grades.csv."`
4. **Menu:**
   ```
   Action:
     1) Add grade
     2) List all
     3) Per-student report
     4) Per-subject report
     5) Exit
   ```
5. **Add grade** — prompt for name (non-blank), subject (non-blank), score (0–100). Append the row, **save the file immediately**, print `"✓ Grade added and saved."`
6. **List all** — table with columns `Name`, `Subject`, `Score`. Rows in file order.
7. **Per-student report** — grouped by student, sorted by name. Columns `Student`, `Subjects`, `Average`.
8. **Per-subject report** — grouped by subject, sorted by subject name. Columns `Subject`, `Grades`, `Average`.
9. **Exit** — print `"ℹ Goodbye."` and exit.
10. **Missing data** — if the file has no grades, print `"ℹ No grades yet."` for any report instead of an empty table.

## Example interaction

```
========================================
│ Student Grade Tracker
========================================
ℹ Created grades.csv with sample data.

Action:
  1) Add grade
  2) List all
  3) Per-student report
  4) Per-subject report
  5) Exit
Your choice [1-5]: 3

Student  Subjects  Average
-------  --------  -------
Alice    2         90.00
Bob      1         75.00
Saad     2         81.50
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| First run | Seed data, save, print "Created" message |
| Second run | Load existing records, print "Loaded N records" |
| File exists but is empty | Treated as first run — seed it |
| Add a grade with valid inputs | Appears in "List all" and both reports on the next menu iteration |
| Add a grade, then exit, then restart | The added grade is still there |
| Manually add a line to `grades.csv` (valid format) | Loaded on next run |
| Manually add a line with fewer than 3 fields | Row is skipped (both versions) |
| Manually add a line with a comma in the name (`Smith, John,Math,80`) | **With-chirp** handles it (proper CSV parsing); **without-chirp** breaks into 4 fields and drops the row |
| Score out of range during input | Re-asked; file unchanged |
| Empty name during input | Re-asked; file unchanged |
| Report when file is empty | `"ℹ No grades yet."` |

The comma-in-name case is a real difference. The without-chirp version uses `split(",")` and assumes fields have no commas. Real CSV supports quoting: `"Smith, John",Math,80` — the with-chirp version parses it correctly.

## The "save immediately" pattern

Every mutation must be followed by a save:

```java
case "Add grade" -> {
    // ... read inputs, validate ...
    records.add(row);
    saveAll(records);  // <-- the important line
    success("Grade added and saved.");
}
```

If the save is deferred to "on exit", a crash, a Ctrl+C, or a kill from the OS loses the last change. Saving immediately means the file is always consistent with the last successful mutation.

The trade-off: writing the whole file on every add is `O(n)` per add, so adding `n` records costs `O(n²)` total. That's fine for the scales of a demo (hundreds of records) and terrible for millions. Every demo in this level chooses the "obviously-correct" behaviour over the "efficient" one; production systems use a database precisely to avoid this trade-off.

## The grouping + averaging pattern

Both reports use the same three-step pipeline:

```java
1. Group the records by a key            (name or subject)
2. Compute the average of each group     (sum of scores / count of scores)
3. Format each group as a table row      (key, count, average)
```

The only thing that differs is *which key* is used. Extract the shared logic into a helper and both reports become one call each:

```java
reportGrouped(records, "name", "Student", "Subjects");
reportGrouped(records, "subject", "Subject", "Grades");
```

That's the pattern the demo implements.

## GUI extension

Build a JavaFX window that:
- Shows the "List all" table as a `TableView<String[]>`
- Has an Add-Grade button that opens a dialog with three fields
- Shows both reports as two `TableView`s side by side (or in tabs)
- Refreshes everything after each add

Same CSV file, same model, different driver.

## What you'll practice

- CSV persistence — read the file, parse the rows, write it back
- First-run handling — check if the file exists before reading
- Immediate save — one operation, one write
- Group + aggregate — the "average per key" pattern
- Trade-off awareness — why writing the whole file per add is fine here and wrong at scale

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 112 | 236 |
| Read file | `CsvFile.readAsRecords(FILE)` | `Files.readAllLines()` + parse each line with `split(",")` |
| Write file | `CsvFile.writeRecords(FILE, records, HEADERS)` | build a `StringBuilder`, `Files.writeString()` |
| First-run check | `records.isEmpty()` — `CsvFile` returns `List.of()` for missing files | `Files.exists(FILE)` check + `try`/`catch` for `IOException` |
| Quoted commas in fields | Handled — `CsvFile` supports RFC 4180 quoting | **Broken** — naive `split(",")` |
| IOException handling | None — helpers return safe defaults | 4 `try`/`catch` blocks, one per file operation |
| Aggregate reports | identical — same `groupBy` + average logic | identical |

The without-chirp version is not just longer — it is *correct-but-fragile*. Add a comma to a name, and it breaks. The with-chirp version handles it because `CsvFile` implements proper parsing.
