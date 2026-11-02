# Hello Greeter

Ask the user for their name and age, then greet them and tell them whether they are an adult.

## Requirements

1. Ask for the user's name. The name must not be blank — whitespace-only input counts as blank, so `"   "` is rejected the same as `""`.
2. Ask for the user's age. The age must be a whole number between **1 and 130** inclusive. Non-numeric input (`"twenty"`), out-of-range input (`0`, `-5`, `200`), and empty input must all be rejected.
3. Invalid input must cause the prompt to be re-asked — the program must never crash on bad input.
4. Print a title bar at the top, using `=` as the border character and `│` as the left column character:

   ```
   ========================================
   │ Hello Greeter
   ========================================
   ```

5. Print a personalised greeting in the form:
   `"✓ Hello, {name}! You are {age} years old."`
6. On the next line, print `"ℹ You're an adult."` if the age is 18 or older, or `"ℹ You're a minor."` otherwise.
7. Before the farewell, print a horizontal separator of 40 `=` characters.
8. Print a farewell line:
   `"✓ Goodbye, {name}!"`

## Example interaction

```
========================================
│ Hello Greeter
========================================
What is your name? Saad
How old are you? 20

✓ Hello, Saad! You are 20 years old.
ℹ You're an adult.
========================================
✓ Goodbye, Saad!
```

## Edge cases to verify

| Input | Expected behaviour |
|---|---|
| Empty name (press Enter) | Re-ask "What is your name?" with a validation message |
| Whitespace-only name (`"   "`) | Re-ask — treated as blank |
| Non-numeric age (`"twenty"`) | Re-ask "How old are you?" with a "whole number" message |
| Age `0` or `131` | Re-ask with a range message |
| Age `18` | Prints `"adult"` |
| Age `17` | Prints `"minor"` |
| Age `130` | Accepted — the upper bound is inclusive |

## What you'll practice

- Reading a string with a non-blank requirement
- Reading an integer within a bounded range
- Branching on a boolean (`isAdult` / `age >= 18`)
- Printing structured output with unicode symbols (`✓`, `ℹ`, `│`)

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 24 | 67 |
| Blank-name validation | `readNonEmpty(prompt)` | hand-rolled `while` loop + `trim().isEmpty()` |
| Age validation | `readInt(prompt, 1, 130)` | hand-rolled `while` loop + `try`/`catch` + range check |
| Title bar | `title("Hello Greeter")` | hand-written `printTitle` helper |
| Success / info lines | `success(msg)`, `info(msg)` | manual string concatenation with unicode |
| Separator | `line()` | `"=".repeat(40)` |

The Chirp version is shorter because every one of those validations is a method call. The `without-chirp` version is not *worse* — it is what a programmer would write if they had never seen a library. Read both.
