# Todo List

Persist a list of todo tasks between runs. Support adding, marking done, deleting, and listing tasks.

## Requirements

1. **File:** `todos.json` in the current working directory. Structure:
   ```json
   [
     {"title": "Buy groceries", "done": false},
     {"title": "Write report", "done": true}
   ]
   ```
2. **First run** (file doesn't exist or is empty array):
   - Seed with these three tasks:
     - Buy groceries / not done
     - Write report / done
     - Call mom / not done
   - Print `"ℹ Created todos.json with sample data."`
3. **Subsequent runs** (file exists with tasks):
   - Load existing tasks
   - Print `"ℹ Loaded N task(s) from todos.json."`
4. **Menu:**
   ```
   Action:
     1) Add task
     2) Mark done
     3) Delete task
     4) List tasks
     5) Exit
   ```
5. **Add task** — prompt for title (non-blank), append with `done=false`, **save immediately**, print `"✓ Task added."`
6. **Mark done** — print the numbered task list first, then prompt for a task number, set `done=true`, save, print `"✓ Marked done."`
7. **Delete task** — print the numbered task list, prompt for a task number, remove, save, print `"✓ Deleted."`
8. **List tasks** — table with columns `#`, `Status`, `Title`. Status is `✓` for done, `○` for not done.
9. **Empty list** — if there are no tasks, print `"ℹ No tasks."` for any listing operation, and reject mark/delete with `"✗ No tasks to modify."`
10. **Exit** — print `"ℹ Goodbye."` and exit.

## Example interaction

```
========================================
│ Todo List
========================================
ℹ Created todos.json with sample data.

Action:
  1) Add task
  2) Mark done
  3) Delete task
  4) List tasks
  5) Exit
Your choice [1-5]: 4

#  Status  Title
-  ------  -------------
1  ○       Buy groceries
2  ✓       Write report
3  ○       Call mom
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| First run | Seed, save, print "Created" message |
| Second run | Load existing tasks |
| File exists but is `[]` | Treated as first run — seed it |
| Add task with blank title | Re-asked; file unchanged |
| Mark task number `0` | Re-asked (range) |
| Mark task number `N+1` | Re-asked (range) |
| Mark an already-done task | Sets `done=true` (idempotent) — no error |
| Delete task 1, then list | Task numbering shifts — task 1 is now what was task 2 |
| Delete all tasks | Empty list on next list operation; mark/delete reject |
| Manually edit the JSON to be invalid | Load fails, program crashes with a clear error (see below) |
| Title containing `|` (with-chirp) | Saved correctly — JSON handles all characters |
| Title containing `|` (without-chirp) | **Broken** — pipe is the field separator |

## The invalid-JSON case — deliberate strictness

If the user manually edits `todos.json` and removes a closing brace, `JsonFile.readArray` **throws** and the program exits with a stack trace. That's intentional:

```java
JsonArray tasks = JsonFile.readArray(FILE);  // throws on malformed JSON
```

A lenient parser (like the CSV reader, which skips bad rows) is the wrong choice for **structured** data — a partially-loaded JSON file is usually a corrupt file, not a file with one bad row. JSON is a strict format, and the parser reflects that. The user sees the failure, fixes the file, and runs again.

If you want lenient loading, wrap the read in a `try`/`catch` and fall back to seed data:

```java
JsonArray tasks;
try {
    tasks = JsonFile.readArray(FILE);
} catch (Exception e) {
    error("Could not read " + FILE + " — starting fresh.");
    tasks = seedDemoTasks();
}
```

The demo does not do this, because the deliberate crash is a teaching point — data corruption should be loud, not silent.

## The "shift-after-delete" lesson

After deleting a task, the remaining tasks re-number from 1. If you delete task 1 of 3, the old task 2 becomes the new task 1. Any UI that references "task 1" after the delete is stale.

This is exactly the index-vs-ID problem real applications face. A database task has a primary key that never changes; a list-based task only has a position, which shifts. If you need stable references, add a `"id"` field to each task. That's a design decision, not a requirement — for a demo, positions are fine.

## GUI extension

Build a JavaFX window that:
- Shows all tasks in a `ListView<String>` with `✓` / `○` prefixes
- Has an inline `TextField` + Add button at the top for new tasks
- Has Mark Done / Delete buttons enabled only when a task is selected
- Refreshes the list after every operation

Same `todos.json`, same model, different driver.

## What you'll practice

- JSON persistence — read the file as a `JsonArray`, mutate it, write it back
- Working with `JsonObject` fields — named access, mutation
- Immediate save after every mutation
- Strict vs. lenient failure handling — why JSON crashes and CSV skips
- The index-vs-ID problem — why position-based references shift

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 93 | 230 |
| File format | JSON — standard, extensible, interoperable | Pipe-delimited text — custom, limited |
| Read file | `JsonFile.readArray(FILE)` | `Files.readAllLines()` + `indexOf('|')` + `substring()` |
| Write file | `JsonFile.writePretty(FILE, tasks)` — one line | `StringBuilder` + `Files.writeString()` — 6 lines |
| Field with `|` | **Preserved** — JSON has no reserved characters | **Breaks** — pipe is the separator |
| Adding a field later | One `put("newField", ...)` — old files still load | Change the format everywhere; old files are incompatible |
| IOException handling | None — helper throws only on malformed JSON | 2 `try`/`catch` blocks, both for `IOException` |
| Task model | `JsonArray` throughout — no conversion | `List<Map<String, String>>` — no conversion |

The JSON version is not just shorter — its format is **forward-compatible**. Add `"priority": "high"` to a task, and the parser still reads the file (ignoring the field on old-style task view). The pipe-delimited format can't add a field without breaking every existing file.
