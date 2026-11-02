# Greeting Window

Type a name and see a personalised greeting update in real time. Show the time of day below.

## Requirements

1. Display a **window** with these controls:
   - A **title label** at the top: `"Greeting Window"`, large and bold
   - A **text field** with placeholder `"Enter your name"`
   - A **greeting label** showing `"Hello, {name}!"`, large and bold
   - A **time-of-day label** below, muted colour, showing `"Good morning!"` / `"Good afternoon!"` / `"Good evening!"`
   - A **Clear button** that resets the text field
2. The greeting label updates **live as the user types** — no button press required.
3. If the field is empty or whitespace-only, show `"Hello, stranger!"`.
4. The time-of-day is computed once at startup:
   - Before 12:00 → `"morning"`
   - 12:00 to 16:59 → `"afternoon"`
   - 17:00 onwards → `"evening"`
5. The window is approximately **400 × 300** pixels, non-resizable is fine.
6. **Console version** — read the name, print the greeting and time-of-day, exit.

## Example — GUI

```
┌─ Greeting Window ────────────────────┐
│                                      │
│         Greeting Window              │
│                                      │
│   ┌────────────────────────────────┐ │
│   │ Saad                           │ │
│   └────────────────────────────────┘ │
│                                      │
│           Hello, Saad!               │
│                                      │
│         Good afternoon!              │
│                                      │
│             [ Clear ]                │
│                                      │
└──────────────────────────────────────┘
```

## Example — console

```
========================================
│ Greeting Window
========================================
Enter your name: Saad

✓ Hello, Saad!
ℹ Good afternoon!
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Empty text field | `"Hello, stranger!"` |
| Text field contains only spaces | `"Hello, stranger!"` |
| Type `Saad` | `"Hello, Saad!"` |
| Type `Saad`, then press Clear | Field empties, label becomes `"Hello, stranger!"` |
| Type `A`, then `Al`, then `Ali` | Label updates three times: `"Hello, A!"` → `"Hello, Al!"` → `"Hello, Ali!"` |
| Type `  Saad  ` (with spaces) | `"Hello, Saad!"` — trim before display, or keep spaces if you prefer |
| Start before noon | `"Good morning!"` |
| Start between noon and 5 PM | `"Good afternoon!"` |
| Start after 5 PM | `"Good evening!"` |

## The "live update" pattern

The single most important GUI idea in this problem: the greeting label updates as the user types, without any button press. That's implemented by:

```java
nameField.onChange(name -> {
    String display = name.isBlank() ? "stranger" : name.trim();
    greetingLabel.text("Hello, " + display + "!");
});
```

`TextField.onChange(...)` is a callback that fires every time the field's content changes — every keystroke, every paste, every delete. The label follows the field within one frame (16 ms), which reads as "instant" to the user.

The alternative — a button that updates the label on click — is what most beginners write. It works, but it feels stiff. Modern GUI applications are largely live: form validation shows errors as you type, search results filter as you type, previews update as you type. The pattern is worth learning on the first GUI.

## The console version is deliberately simpler

The console version reads one name, prints one greeting, exits. It does not loop for a second greeting. Why?

Because **there's nothing to compare**. The point of this problem is the GUI — the live update, the layout, the button. The console version exists to show that the *underlying logic* is identical: take a name, build a greeting, pick a time of day. The console version can't loop-for-live-update because the console has no "live" — it processes one line and echoes once.

That asymmetry is the lesson: **the GUI is not "console plus a window"**. It's a different interaction model, and the console version is the honest baseline.

## The four variants compared

| Concern | with-chirp console | without-chirp console | with-chirp-gui | without-chirp-gui |
|---|---|---|---|---|
| Lines | 23 | 34 | 40 | 60 |
| Read name | `readLine(prompt)` | `Scanner.nextLine()` + retry loop for input exhaustion | n/a — typed in a field | n/a |
| Print greeting | `success(msg)` | `System.out.println("✓ " + msg)` | `label.text(msg)` | `label.setText(msg)` |
| Clear field | n/a | n/a | `nameField.text("")` + `requestFocus` | `nameField.clear()` + `requestFocus` |
| Layout | implicit | implicit | `Stack.v(12).align(CENTER)` | `new VBox(12, ...)` + `setAlignment(CENTER)` + `setPadding(Insets)` |
| Style | console symbols | console symbols | Chirp variant (`Label.heading`, `Label.muted`) | Inline CSS (`setStyle("-fx-font-size: 24px; ...")`) |
| Entry point | `main()` | `main()` | `Ui.app(() -> ...)` | `Application.start(Stage)` |

The largest surprise for beginners is usually the style row — the with-chirp-gui version says `Label.heading(...)` and the without-chirp-gui version has to write a CSS string. Both produce the same visual result; only one is read by a human without counting pixels.

---
