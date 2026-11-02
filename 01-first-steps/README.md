# Level 1 — First Steps

The three programs every beginner writes. No loops, no classes, no files — just input, output, and formatted results.

## What this level covers

- Reading a string, an integer, and a decimal from the user
- Validating input — blank checks, range checks, decimal parsing
- Choosing from a fixed set of options with a numbered menu
- Branching on a condition (`if` / `else`, `switch`)
- Formatting output with symbols, separators, and fixed-precision numbers

There are no `for` or `while` loops in this level on purpose. A retry-on-invalid loop is *implicit* inside Chirp's `readInt`/`readDouble`/`readNonEmpty`, and the `without-chirp` versions write that loop by hand — but the programmer doesn't have to *design* one yet. That comes in Level 3.

## The three problems

| Problem | Concepts | With Chirp | Without Chirp |
|---|---|---|---|
| [01-hello-greeter](01-hello-greeter/) | string + int input, adult/minor branching | 18 lines | 45 lines |
| [02-temperature-converter](02-temperature-converter/) | decimal input with a bound, choice menu, formatted output | 40 lines | 70 lines |
| [03-simple-calculator](03-simple-calculator/) | operator menu, `switch` on strings, error-case handling | 30 lines | 65 lines |

The line counts are the actual file sizes. The "without" versions write three reusable helpers by hand — a non-blank reader, a range-checked integer reader, and a menu — and those helpers get copy-pasted across every example. That copy-paste is the first honest signal that the programmer needs a library.

## Dependencies

- **`with-chirp/`** variants depend on `chirp-1.0.0.jar`.
- **`without-chirp/`** variants have no dependencies.

## What's not here yet

- **No GUI.** JavaFX starts at Level 5.
- **No files.** That's Level 6.
- **No user-written loops.** That's Level 3.
