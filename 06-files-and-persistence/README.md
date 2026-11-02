# Level 6 — Files & Persistence

Programs that remember. Every problem in this level reads from a file on startup and writes back when the user changes something, so the data survives between runs.

## What this level covers

- **Read → modify → write** — load a whole file, mutate the in-memory representation, save the whole file
- **Append-only logs** — add one line per event without rewriting existing data
- **First-run handling** — what the program does when the file does not exist yet
- **Malformed-data handling** — reading a file that has been hand-edited and is now invalid
- **Format choice** — why CSV vs. JSON vs. delimited text each have a place
- **Human-readable persistence** — the file is inspectable and editable in a text editor

## What it doesn't cover yet

- **Databases** — a real application would reach for SQLite or Postgres long before hand-rolled CSV. Level 6 is about the *concept* of persistence, not production storage.
- **Concurrent access** — no locking, no atomic writes. Two instances of the program writing at the same time will corrupt the file.
- **Large files** — everything is loaded into memory at once. Fine for a few thousand rows; wrong for millions.
- **GUI** — that's Level 8.

## The three problems

| Problem | File pattern | Format | With Chirp | Without Chirp |
|---|---|---|---|---|
| [01-student-grade-tracker](01-student-grade-tracker/) | Read → modify → write | CSV | ~95 lines | ~150 lines |
| [02-todo-list](02-todo-list/) | Read → modify → write | JSON | ~110 lines | ~155 lines |
| [03-expense-tracker](03-expense-tracker/) | Append-only log | Delimited text | ~100 lines | ~145 lines |

## The `with-chirp` advantage in this level

Levels 1–5 showed Chirp saving *input plumbing*. Level 6 is the first level where Chirp saves **the actual domain work** — parsing and writing files is the problem, and Chirp carries it.

Three specific things the without-chirp version writes by hand:

1. **Format parsing** — a mini-CSV reader, a JSON reader, a text-line reader. Each has to handle missing files, empty files, blank lines, and malformed lines.
2. **Format writing** — the inverse. Every writer has to handle escaping (commas in CSV, quotes in JSON, newlines everywhere).
3. **IOException handling** — every `Files.readString`, `Files.writeString`, `Files.readAllLines` throws a **checked** exception. Every call site needs `try`/`catch` or a wrapper method.

Chirp's file helpers return safe defaults (`""`, empty list, `false`) on failure. The caller never sees a checked exception. That's not a small thing — it's 30–50 lines of boilerplate that vanishes.

## Why not JSON everywhere?

Three formats, one per problem, is deliberate:

- **CSV** is the right format for **tabular data** — flat rows of the same shape. Graders, expenses, exports from spreadsheets.
- **JSON** is the right format for **structured data with optional fields** — the todo's `done` might one day become `{status, completedAt, tags}`. JSON handles that without changing the top-level shape.
- **Delimited text** is the right format for **append-only logs** — one event per line, readable by `grep`, `awk`, `less`.

Students learn that "which format?" is a real question, not "always use JSON".

## The first-run problem

Every demo here seeds some sample data on first run so the reports have something to show. Look for the pattern:

```java
if (records.isEmpty()) {
    records = seedDemoData();
    saveAll(records);
    info("Created " + FILE + " with sample data.");
} else {
    info("Loaded " + records.size() + " record(s) from " + FILE + ".");
}
```

That handles four cases with three lines:
- File doesn't exist → seed, save, tell the user
- File exists but empty → same as above
- File exists with data → load, tell the user
- File exists but is corrupt → depends on the reader (CSV ignores bad lines, JSON throws)

That last case is where format choice shows through. JSON is strict; a single malformed byte and the whole file fails to load. CSV is lenient; a bad row is skipped, the rest loads. Neither is "better" — it's a trade-off between safety and resilience.

## The three "gotchas" that trip up beginners

1. **Forgetting to save.** The most common persistence bug: the user adds a record, the program forgets to call `saveAll()`, the program exits, the data is gone. Every write operation in these examples calls `saveAll(records)` **immediately** after mutating the list, so a crash mid-session still keeps the completed changes.
2. **Reading before the first run.** `Files.readAllLines` throws `NoSuchFileException` if the file doesn't exist. Every reader in this level checks `Files.exists(file)` (or Chirp's `TextFileIO.exists`) first, and treats "missing" as "empty".
3. **Manually editing the file to test.** Users will open the CSV in a text editor, add a row, save, and reload. If your parser isn't robust, that breaks the program. Test it: manually add a row to `grades.csv` with an extra space, or a comma-separated name, and see what happens.

## GUI extension

Each problem README includes a **GUI extension** section describing what the same model should look like as a JavaFX window. The GUI code is being delivered as a separate batch after the console versions. Same model, same files, different driver.
