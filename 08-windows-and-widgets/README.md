# Level 8 — Windows & Widgets

The first graphical level. Every problem ships **four drivers** of the same underlying logic:

- `with-chirp/` — console, using Chirp's input helpers
- `without-chirp/` — console, using plain `Scanner`
- `with-chirp-gui/` — JavaFX window, using Chirp's UI wrappers
- `without-chirp-gui/` — JavaFX window, using raw JavaFX

The console versions are short — you've already seen this style in Levels 1–7. The GUI versions are the point of this level: the with-chirp-gui variant is 60–130 lines, the without-chirp-gui variant is 120–230 lines of the same behaviour.

## JavaFX setup — read this first

JavaFX is **not bundled with the JDK** since Java 11. Running a JavaFX example needs one of two setups:

**With Chirp** — the `chirp-1.0.0.jar` is a fat jar that bundles JavaFX (with platform natives). If it's on your classpath, the GUI classes resolve automatically:

```
java --class-path chirp-1.0.0.jar GreetingWindowGui.java
```

**Without Chirp** — download a JavaFX SDK from [openjfx.io](https://openjfx.io/) and pass it on the module path:

```
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.graphics,javafx.base \
     GreetingWindowGui.java
```

On Windows, use `;` instead of `:` in the classpath or module path. On macOS and Linux, use `:`.

If the with-chirp GUI version fails to find a JavaFX native library on first launch, fall back to the without-chirp command — the SDK approach works for both.

## What this level covers

- **Window anatomy** — a title, a scene, a root layout, at least one control
- **Layouts** — vertical stacking, horizontal rows, centred content
- **Controls** — text fields, labels, buttons, list views
- **Events** — a button click, a text field typing event, a list selection
- **Reactive updates** — how to keep the UI in sync when state changes
- **Persistence** — saving data between runs, refreshed after every mutation

## What this level does NOT cover

- **FXML** — declarative UI in XML. Chirp uses programmatic construction throughout.
- **Custom controls** — building a control from scratch. Every example uses standard controls.
- **Animations** — transitions, timelines, keyframe animations.
- **Threading** — the demos use JavaFX's built-in single-threaded model. `Ui.runAsync` is available for network calls but not shown here.

## The four-driver comparison

Look at the same problem in all four drivers for a specific lens on what a library does:

| Aspect | with-chirp (console) | without-chirp (console) | with-chirp-gui | without-chirp-gui |
|---|---|---|---|---|
| **Who runs it** | terminal user | terminal user | desktop user | desktop user |
| **Interaction** | prompts + typed input | prompts + typed input | click, type, see | click, type, see |
| **Layout** | implicit (line order) | implicit (line order) | explicit (VBox, HBox) | explicit (VBox, HBox) |
| **Feedback** | one screen at a time | one screen at a time | live, in place | live, in place |
| **Code size** | 20–40 lines | 50–90 lines | 60–130 lines | 120–230 lines |
| **Boilerplate** | input prompts | input prompts + validation loops | layout + event wiring | layout + event wiring + style strings |

The console-to-GUI jump doubles the code in the best case (Chirp) and triples it in the worst (raw JavaFX). The reason is that a GUI has to explicitly construct what a console implies: where things go, what they look like, what happens when you click them.

## The three problems

| Problem | GUI focus | with-chirp-gui | without-chirp-gui |
|---|---|---|---|
| [01-greeting-window](01-greeting-window/) | Text field + live label | ~50 lines | ~90 lines |
| [02-temperature-converter-gui](02-temperature-converter-gui/) | Numeric input + live output | ~45 lines | ~85 lines |
| [03-todo-app-gui](03-todo-app-gui/) | List view + CRUD + persistence | ~100 lines | ~210 lines |

The ratio widens with each problem — a greeting is a few controls; a todo app with persistence is a full data-driven UI. That's the honest shape of GUI code.

## The three "gotchas" that trip up beginners

1. **The window must be created on the JavaFX thread.** `Ui.show(...)` throws `IllegalStateException` if called outside `Platform.isFxApplicationThread()`. The `Ui.app(() -> { ... })` entry point takes care of this — everything inside the lambda runs on the FX thread.
2. **State changes don't update the UI automatically.** Unlike console code, where `System.out.println` prints to screen immediately, a `Label` in JavaFX only changes when you call `label.setText(...)` or `label.text(...)`. Every GUI example wires up an event listener that calls the update method.
3. **Layout matters.** A `VBox` stacks children vertically by default, but a child only expands horizontally if you set `HBox.setHgrow(node, Priority.ALWAYS)` or `.setMaxWidth(Double.MAX_VALUE)`. The examples show both patterns — the with-chirp version uses `.fullWidth()`, the without-chirp version uses `setMaxWidth(...)` explicitly.
