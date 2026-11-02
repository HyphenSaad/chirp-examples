# Todo App GUI

A graphical todo list with add, delete, and persistent storage.

## Requirements

1. Display a **window** with these controls:
   - A **title label**: `"Todo List"`, large and bold
   - A **text field** with placeholder `"What needs doing?"`
   - An **Add button** next to the text field, primary style
   - A **list view** showing all tasks, scrollable
   - A **Delete selected button** below the list, outline style, disabled when no selection
2. **Add task** — press Add button or press Enter in the field. Rejects blank titles. After adding, clear the field and save the file.
3. **Delete task** — select a task in the list, click Delete. Removes it and saves the file.
4. **Persistence** — save to `todos.txt` in the current directory, one task title per line. Load on startup. Save after every add or delete.
5. **Empty list** — when there are no tasks, the list view is empty and the delete button stays disabled.
6. **Window size** — approximately **500 × 500** pixels.

## Example — GUI

```
┌─ Todo List ──────────────────────────┐
│                                      │
│            Todo List                 │
│                                      │
│  ┌──────────────────────┐ [ Add ]    │
│  │ Buy groceries        │            │
│  └──────────────────────┘            │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Buy groceries                  │  │
│  │ Write report                   │  │
│  │ Call mom                       │  │
│  │ ▸ Read chapter 3               │  │  ← selected
│  │ Finish project                 │  │
│  │                                │  │
│  └────────────────────────────────┘  │
│                                      │
│  [ Delete selected ]                 │
│                                      │
└──────────────────────────────────────┘
```

The delete button is enabled only when a task is selected. When nothing is selected, the button is disabled (greyed out).

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Empty text field, press Add | No task added; field stays empty |
| Text field has only spaces, press Add | No task added — trim before checking |
| Type `Buy milk`, press Add | Task appears at the bottom of the list; field clears |
| Type `Buy milk`, press Enter | Same as pressing Add |
| Select a task, click Delete | Task is removed from the list |
| No task selected, click Delete | Button is disabled; click does nothing |
| Add 3 tasks, close window, reopen | All 3 tasks are loaded from `todos.txt` |
| Delete a task, close window, reopen | Deleted task stays deleted |
| `todos.txt` doesn't exist on startup | List starts empty (no error) |
| `todos.txt` has 5 lines, file opened in editor | 5 tasks appear in the list |
| Manually add a line to `todos.txt` while app is running | Not loaded until restart (the app doesn't watch the file) |

## The "reactive list" pattern

The list view holds an **observable** collection. When the collection changes, the list view updates automatically:

```java
ObservableList<String> tasks = FXCollections.observableArrayList();
taskList.setItems(tasks);

// Adding a task
tasks.add("Buy milk");   // ← the list view updates itself

// Deleting a task
tasks.remove(index);     // ← the list view updates itself
```

That's the difference from the console version — where adding to a `List` requires a separate `System.out.println` to see the change, here the mutation alone is enough. `ObservableList` is the mechanism: any change to it fires events that the `ListView` is subscribed to.

If you had used a plain `ArrayList<String>`, the list view would only show the state at the moment you called `setItems(...)` — later mutations would be invisible until you forced a refresh. That's a common bug in first GUI attempts.

## The selection-to-button wiring

The delete button is disabled unless a task is selected. That's three lines:

```java
deleteButton.disabled(true);   // start disabled

taskList.getSelectionModel().selectedItemProperty().addListener(
    (obs, old, next) -> deleteButton.disabled(next == null));
```

That wiring is a mini-lesson in reactive UI: one piece of state (`selectedItem`) drives another (`deleteButton.disabled`). Any time the selection changes — up, down, click, keyboard arrow — the button toggles. No manual synchronisation on every user action.

The alternative — enable the button on click, disable on click-elsewhere — is impossible to get right. The listener pattern is the only correct way.

## The "save on every change" pattern

Persistence happens on **every mutation**, not on window close:

```java
private static void saveTasks(ObservableList<String> tasks) {
    StringBuilder sb = new StringBuilder();
    for (String t : tasks) sb.append(t).append('\n');
    TextFileIO.write(FILE, sb.toString());
}
```

Called after every `tasks.add(...)` and `tasks.remove(...)`. If the user force-quits the app (Alt+F4, kill), the last successful change is already on disk.

The alternative — save on `stage.setOnCloseRequest(...)` — is fragile: a crash, a kill, or an OS reboot loses everything since the last save. Real applications save eagerly.

## Why persistence here uses plain text, not JSON

Level 6 covered JSON persistence. Level 8's todo app uses plain text (one task per line) to keep the comparison focused on the **GUI code** rather than on the serialization format.

If you want JSON — or a task model with `done` status, priority, due dates — extend the model in the same way. The GUI code doesn't change; only the load/save functions do.

## The four variants compared

| Concern | with-chirp console | without-chirp console | with-chirp-gui | without-chirp-gui |
|---|---|---|---|---|
| Lines | 61 | 143 | 67 | 105 |
| Add task | `readNonEmpty(prompt)` + list add | `Scanner.nextLine()` + trim + list add | `textField.value()` + `tasks.add(...)` | `textField.getText()` + `tasks.add(...)` |
| List view | manual loop printing each task | manual loop printing each task | `ListView.of()` + `setItems(tasks)` | `new ListView<String>()` + `setItems(...)` |
| Selection | prompt for task number | prompt for task number | `getSelectionModel().selectedItemProperty()` | same |
| Delete button state | n/a — no disabled concept | n/a | `deleteButton.disabled(...)` listener | `deleteButton.setDisable(...)` listener |
| Layout | implicit | implicit | `Stack.v(12).padding(20)` + `Row.h(8)` | `new VBox(12, ...)` + `new HBox(8, ...)` + `HBox.setHgrow(...)` |
| Persistence | `TextFileIO.readLines` + `write` | `Files.readAllLines` + `try`/`catch` | same as console | same as console |
| Entry point | `main()` | `main()` | `Ui.app(() -> ...)` | `Application.start(Stage)` |

The line count difference is dramatic — 210 versus 100 — because raw JavaFX requires more ceremony for layout, padding, Hgrow, and styling. The behavioural difference is zero.
