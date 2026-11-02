# Rock Paper Scissors

Play one round of rock paper scissors against the computer. The user picks a move via menu; the computer picks at random; the program decides who won.

## Requirements

1. Ask for the user's move using a menu with exactly three options: `Rock`, `Paper`, `Scissors`. Invalid choices must be rejected and re-asked.
2. Compute the computer's move using random selection from the same three options.
3. Decide the outcome using these rules:
   - **Same move from both** → draw
   - **Rock beats scissors** → user wins if they chose rock and the computer chose scissors
   - **Paper beats rock** → user wins if they chose paper and the computer chose rock
   - **Scissors beats paper** → user wins if they chose scissors and the computer chose paper
   - **Any other combination** → user loses
4. Print both moves on one line:
   `"ℹ You: {user}  |  Computer: {computer}"`
5. Print a separator line.
6. Print the result on its own line:
   - Win: `"✓ You win!"`
   - Loss: `"✗ You lose."`
   - Draw: `"ℹ It's a draw."`

## Example interaction

**User wins:**
```
========================================
│ Rock Paper Scissors
========================================
Your move:
  1) Rock
  2) Paper
  3) Scissors
Your choice [1-3]: 1

ℹ You: Rock  |  Computer: Scissors
========================================
✓ You win!
```

**Draw:**
```
Your choice [1-3]: 2

ℹ You: Paper  |  Computer: Paper
========================================
ℹ It's a draw.
```

## The full outcome matrix

Nine combinations — every one must be handled correctly:

| User ↓ vs Computer → | Rock | Paper | Scissors |
|---|---|---|---|
| **Rock** | Draw | Lose | **Win** |
| **Paper** | **Win** | Draw | Lose |
| **Scissors** | Lose | **Win** | Draw |

Three wins on the diagonal-to-a-corner pattern, three draws on the main diagonal, three losses everywhere else.

## The "one-way comparison is enough" lesson

You don't need to enumerate all nine outcomes. A three-step decision reaches the same answer with much less code:

1. **Same move?** → draw. Nothing else to check.
2. **Is the user's move one of the three winning pairs?** → win.
3. **Otherwise** → loss. There's no fourth possibility.

```java
if (user.equals(computer)) return "Draw";
if ((user.equals("Rock")     && computer.equals("Scissors")) ||
    (user.equals("Paper")    && computer.equals("Rock"))     ||
    (user.equals("Scissors") && computer.equals("Paper"))) {
    return "Win";
}
return "Loss";
```

That last `return "Loss"` is a **guard clause** — the exhaustive `if` above covers every winning combination, so anything left over must be a loss. This is far more readable than a nine-way `switch` or a lookup table, and it's a pattern the student will reuse every time they compare two values against a matrix of outcomes (permissions, precedence, priority — all the same shape).

There's also an alternative trick worth knowing:

```java
// "Beats" relation expressed as index arithmetic — works because
// Rock(0) beats Scissors(2), Paper(1) beats Rock(0), Scissors(2) beats Paper(1).
// That's (user - computer + 3) % 3 == 2.
int userIdx = indexOf(MOVES, user);
int compIdx = indexOf(MOVES, computer);
if (userIdx == compIdx) return "Draw";
return ((userIdx - compIdx + 3) % 3 == 2) ? "Win" : "Loss";
```

This is elegant but obscure. Use it only if you can explain *why* the modulo arithmetic works — otherwise the explicit `if` chain is clearer. That trade-off is worth a class discussion; the explicit version is what a reviewer would prefer.

## Edge cases to verify

| Scenario | Expected outcome |
|---|---|
| User picks Rock, computer picks Scissors | Win |
| User picks Paper, computer picks Rock | Win |
| User picks Scissors, computer picks Paper | Win |
| User picks Rock, computer picks Paper | Loss |
| User picks Paper, computer picks Scissors | Loss |
| User picks Scissors, computer picks Rock | Loss |
| Both pick the same move | Draw |
| Menu choice `9` | Re-asked |
| Empty menu input | Re-asked |

## Note on randomness

The computer's move is **random**, so consecutive runs may differ. The *decision logic* — the `compare` method — is deterministic; given the same pair of moves, it always returns the same result. If you're testing, test `compare("Rock", "Scissors")` directly rather than running the full program and hoping for a specific outcome.

## What you'll practice

- Comparing two values against a matrix of outcomes
- Writing a decision that returns **early** for the trivial cases (draw) so the rest of the logic stays flat
- Recognising when exhaustive enumeration (nine cases) is unnecessary
- Guard clauses — the final `return "Loss"` that catches everything the win-`if` missed

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 39 | 74 |
| User move menu | `readChoice("Your move:", MOVES)` | hand-rolled menu loop + range check |
| Computer move | `MOVES[random(0, 2)]` | `new Random().nextInt(MOVES.length)` |
| Result line | `success(...)` / `error(...)` / `info(...)` | manual `System.out.println` |
| Decision logic | identical | identical |

The decision logic is the same in both versions — that's the problem. The 40-line gap is the menu reader (copied from Level 1, again) plus manual output.
