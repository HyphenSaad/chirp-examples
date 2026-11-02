# Bank Account

Build a `BankAccount` class with private state and safe mutation methods, then drive it from a menu.

## Requirements — the class

Design a class `BankAccount` with:

1. **Private fields** — `String owner`, `double balance`, `List<String> transactions`. No public fields. No public setters.
2. **Constructor** `BankAccount(String owner, double initialBalance)`:
   - `owner` must be non-null and non-blank → throw `IllegalArgumentException` otherwise
   - `initialBalance` must be `>= 0` → throw `IllegalArgumentException` otherwise
   - Record an initial transaction line: `"Opened with {balance}"`
3. **Accessors:**
   - `owner()` — returns the owner string
   - `balance()` — returns the current balance
   - `transactions()` — returns an **immutable copy** of the transaction list (`List.copyOf`)
4. **`deposit(double amount)`** — returns `boolean`:
   - `false` if `amount <= 0` (nothing changes)
   - `true` otherwise (balance increases, transaction logged)
5. **`withdraw(double amount)`** — returns `boolean`:
   - `false` if `amount <= 0`
   - `false` if `amount > balance` — never allow negative balance
   - `true` otherwise (balance decreases, transaction logged)
6. Every successful deposit or withdrawal appends a transaction line in the format:
   `"Deposit Rs.X,XXX.XX — balance Rs.X,XXX.XX"`
   `"Withdraw Rs.X,XXX.XX — balance Rs.X,XXX.XX"`

## Requirements — the demo

1. Create a `BankAccount` for **"Saad"** with an initial balance of **1000.00**.
2. Print a title bar and a line showing the opening: `"ℹ Opened account for Saad with Rs.1,000.00"`.
3. Repeatedly show a menu:
   ```
   Action:
     1) Deposit
     2) Withdraw
     3) View balance
     4) View history
     5) Exit
   ```
4. Each menu action:
   - **Deposit** — prompt for amount (must be `> 0`), call `deposit()`, print `"✓ Deposited Rs.X — balance Rs.Y"`
   - **Withdraw** — prompt for amount (must be `> 0`), call `withdraw()`, print `"✓ Withdrew Rs.X — balance Rs.Y"` on success, or `"✗ Insufficient funds — balance is Rs.Y"` on failure
   - **View balance** — print `"ℹ Current balance: Rs.Y"`
   - **View history** — print every transaction line prefixed with `"ℹ "`
   - **Exit** — print `"ℹ Goodbye, Saad."` and exit
5. After each action, print a blank line before the next menu.

## Example interaction

```
========================================
│ Bank Account Demo
========================================
ℹ Opened account for Saad with Rs.1,000.00

Action:
  1) Deposit
  2) Withdraw
  3) View balance
  4) View history
  5) Exit
Your choice [1-5]: 1
Amount: 500

✓ Deposited Rs.500.00 — balance Rs.1,500.00

Action:
  1) Deposit
  2) Withdraw
  3) View balance
  4) View history
  5) Exit
Your choice [1-5]: 2
Amount: 2000

✗ Insufficient funds — balance is Rs.1,500.00
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Construct with `"Saad", 0` | Accepted — starting from zero is valid |
| Construct with `"", 1000` | `IllegalArgumentException` |
| Construct with `"Saad", -1` | `IllegalArgumentException` |
| `deposit(500)` | Balance increases by 500, transaction logged |
| `deposit(0)` | Returns `false`, balance unchanged |
| `deposit(-100)` | Returns `false`, balance unchanged |
| `withdraw(balance)` | Balance becomes 0 — success |
| `withdraw(balance + 0.01)` | Returns `false`, balance unchanged |
| Multiple deposits | All logged, all visible in history |

## The "insufficient funds" decision

The requirement says `withdraw()` **returns `false`** when there aren't enough funds. It does **not** throw. Why?

- **Returning `false` is testable** — `assertFalse(account.withdraw(2000))` runs in milliseconds.
- **Throwing is a different contract** — `try { withdraw(...) } catch (...) {}` pushes the failure handling up to the caller, which forces every call site to wrap the call.
- **The demo does both** — `withdraw()` returns `false`, and the demo *converts* that into a user-facing error message via `error("Insufficient funds")`.

The pattern: **the model signals failure through its return value; the view decides how to present it.** A console view shows a message. A GUI view would show a red label or a dialog. A test asserts on the boolean. Same model, three consumers, no exceptions involved.

That's a choice worth discussing — many production systems would throw instead. Both are correct, but "return boolean" makes for a simpler example and clearer separation of concerns.

## The "immutable history" decision

`transactions()` returns `List.copyOf(transactions)`, not the list itself. This is deliberate:

```java
List<String> history = account.transactions();
history.clear();  // if this were the real list, this would wipe the account's records
```

With the copy, `history.clear()` affects only the local copy. The account is safe. The cost is one `O(n)` copy per call — irrelevant at the scale of a demo, and worth it for the invariant "the caller cannot corrupt the history."

Every getter that returns a collection in this level uses the same defence. That's a habit worth building early.

## GUI extension

Build a JavaFX window that:
- Shows the balance in a large heading label
- Has a text field for the amount
- Has a Deposit button (primary) and a Withdraw button (secondary)
- Has a `ListView<String>` showing the transaction history
- Refreshes the balance label and history list after every operation

The `BankAccount` class **does not change** — only the driver does. This is the payoff of keeping the model pure: the same object can power both a console and a window.

## What you'll practice

- Private fields + constructor validation
- Accessor methods that return safe copies
- Boolean-returning state-mutating methods
- Keeping the model free of console/UI dependencies
- A menu-driven driver loop

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 85 | 152 |
| Model class (`BankAccount`) | **identical** — pure Java | **identical** |
| Menu selection | `readChoice("Action:", ...)` | hand-rolled menu loop |
| Amount input (min bound) | `readDouble("Amount").min(0.01).read()` | hand-rolled `while` + `try`/`catch` |
| Currency format | `rupees(amount)` | `"Rs." + String.format("%,.2f", amount)` |
| Success/error/info lines | `success(...)`, `error(...)`, `info(...)` | manual `System.out.println("✓ " + ...)` |

The model is byte-for-byte the same in both versions — that's the point. The 35-line difference is entirely the input and output plumbing.
