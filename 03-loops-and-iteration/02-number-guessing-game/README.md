# Number Guessing Game

The program picks a random number between 1 and 100. The user guesses until correct. After each wrong guess, the program says whether the guess was too high or too low.

## Requirements

1. Generate a **random target** between **1 and 100** inclusive.
2. Announce the rules: `"ℹ I'm thinking of a number between 1 and 100."`
3. Loop asking for a **guess**. Each guess must be a whole number between 1 and 100 — out-of-range or non-numeric input is re-asked **without counting as an attempt**.
4. After each wrong guess, print one of:
   - `"⚠ Too low. Try higher."` — if the guess is less than the target
   - `"⚠ Too high. Try lower."` — if the guess is greater than the target
5. When the guess matches the target, print:
   - `"✓ Correct! You guessed it in N attempts."` (singular `"attempt"` if N = 1)
6. End the program after a correct guess. There is **no attempt limit** — a game only ends when the user guesses correctly or interrupts with Ctrl+C.

## Example interaction

```
========================================
│ Number Guessing Game
========================================
ℹ I'm thinking of a number between 1 and 100.

Your guess: 50
⚠ Too low. Try higher.

Your guess: 75
⚠ Too high. Try lower.

Your guess: 62
⚠ Too low. Try higher.

Your guess: 68
✓ Correct! You guessed it in 4 attempts.
```

## Boundary cases you must check

| Case | Expected behaviour |
|---|---|
| First guess is correct | `"Correct! You guessed it in 1 attempt."` — singular |
| Two attempts | `"Correct! You guessed it in 2 attempts."` — plural |
| Guess `0` | Re-asked as range error — does **not** count as attempt |
| Guess `101` | Re-asked as range error — does **not** count as attempt |
| Guess `"fifty"` | Re-asked as number error — does **not** count as attempt |
| Guess below target | `"⚠ Too low. Try higher."` |
| Guess above target | `"⚠ Too high. Try lower."` |

The attempt counter behavior is intentional — invalid guesses are **typos, not guesses**. A player who types `500` hasn't made an attempt; they've made a mistake. A real guessing game should be forgiving on typos and strict on guesses.

## The `while (true)` + `return` pattern

The main loop has no exit condition in its header:

```java
while (true) {
    // ... get a guess, check it, print a hint ...
}
```

The exit is a **`return`** inside the loop, fired when the guess is correct:

```java
if (guess == target) {
    success("Correct!");
    return;
}
```

This shape is common when:
- The exit condition is checked *after* some work is done
- The exit requires setup (reading a value, checking multiple conditions)
- The exit is a one-time event, not a loop-boundary check

Alternatives:
- `boolean won = false; while (!won) { ... won = (guess == target); ... }` — more verbose, but easier to reason about the exit condition
- `for (;;)` — same as `while (true)`, slightly less idiomatic in modern Java

`while (true)` with early return is the cleanest form when the loop body's *purpose* is "get a valid guess". It reads as: "keep asking until the user provides the right answer, then stop." That's exactly the mental model of the game.

## The "smart" strategy (bonus reading)

A user following binary search solves any 1–100 target in **at most 7 guesses**:

1. Guess 50.
2. If too low, guess 75. If too high, guess 25.
3. Continue halving the remaining range.

The maximum number of guesses is `⌈log₂(100)⌉ = 7`. A user who just guesses randomly might take 50+ guesses. The program can't detect which strategy the user is following — but a curious student might notice the pattern and ask why.

If they do, the answer is: **binary search is how you find a value in any ordered range in `O(log n)` time**, and it's the algorithm behind `Arrays.binarySearch`, database indexes, and half of every "how do I find X in this sorted list?" problem. The guessing game is a playground for the same idea.

## What you'll practice

- **`while (true)` loops with early return** — repeat until a goal is met
- **Loop invariants** — the target doesn't change; only the guesses do
- **Counting inside a loop** — the attempt counter, incremented once per valid guess
- **Random generation** — the target must be uniformly distributed across `[1, 100]`
- **Nested decision inside a loop** — three branches (equal / less / greater) inside the loop body

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 30 | 61 |
| Random target | `random(1, 100)` | `new Random().nextInt(100) + 1` |
| Guess input | `readInt("Your guess", 1, 100)` — invalid guesses are re-asked for free | hand-rolled `while` + `try`/`catch` + range check |
| Hint messages | `warn("Too low. Try higher.")` | manual `System.out.println("⚠ ...")` |
| Success message | `success("Correct! ...")` | manual `System.out.println("✓ ...")` |

The with-chirp version is shorter on the *surrounding* code, not the loop logic. The `while (true)`, the three branches, and the `attempts++` are identical between the two versions — because they're the lesson.
