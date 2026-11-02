# Level 5 — Building Classes

The first level where the student writes the classes, not just the code that uses them. Every problem here has one thing in common: the interesting logic lives inside a class with private state and methods that modify it. The `main` method becomes a thin driver.

## What this level covers

- **Fields and constructors** — private state, validated at construction
- **Encapsulation** — public methods that operate on private fields, no public setters
- **State mutation** — `deposit`, `addItem`, `checkout` all change the object
- **Immutable building blocks** — records for values that don't change (`Item`, `Grade`)
- **Class invariants** — "balance never goes negative", "you can't remove an item that isn't there"
- **Single-file demonstration** — the model class is a `static class` inside the demo, so each example is one file

## What this level does NOT cover

- **Inheritance** — no subclasses, no `extends`. That's a topic for a later level.
- **Interfaces** — no `implements`. Same.
- **Files, network, GUI** — Levels 6–8.

## The three problems

| Problem | Class you write | What it teaches | With Chirp | Without Chirp |
|---|---|---|---|---|
| [01-bank-account](01-bank-account/) | `BankAccount` | Encapsulation, invariants | ~75 lines | ~110 lines |
| [02-shopping-cart](02-shopping-cart/) | `ShoppingCart` + `Item` record | Mutable container, immutable value | ~110 lines | ~160 lines |
| [03-library-book](03-library-book/) | `Book` | State machine (available / borrowed), dates | ~100 lines | ~145 lines |

## The "model is pure Java" pattern

Look at each problem's `with-chirp` version. The nested `BankAccount` / `ShoppingCart` / `Book` class contains **no references to Chirp at all**. It uses plain Java — `ArrayList`, `String.format`, `LocalDate`. Chirp appears only in the demo's `main` method, where it handles the input/printing.

This is a deliberate teaching point:

1. **The model is portable.** Copy `BankAccount` into a Spring Boot app, an Android app, a JavaFX app — it compiles unchanged. It has zero dependencies.
2. **The view is replaceable.** Change the demo from console to GUI to web, and the model doesn't change. That's the OOP goal — the domain logic doesn't care how it's presented.
3. **You could test it without a console.** `new BankAccount("Saad", 1000).deposit(500)` doesn't need `Scanner`, doesn't need `System.out` — it's just an object.

The demo code is where Chirp helps; the model is where it stays out of the way.

## Why single-file nested classes here

In a real project, `BankAccount` would be `BankAccount.java` and `BankAccountDemo` would `import chirp.examples.bankaccount.BankAccount`. But every earlier level has been single-file — `java FileName.java` runs the whole thing. Keeping that property means the model lives inside the demo as a nested class.

If you're building a real application, extract the model. The examples here are teaching aids, not a project template.

## The three "gotchas" that trip up beginners

1. **Public fields.** `public double balance;` looks simpler than `private double balance; getBalance()`, but it lets any code set `account.balance = -500`. Every field in this level is private. There are zero public setters. If state changes, it goes through a method that can validate.
2. **No validation in the constructor.** `new BankAccount("", -100)` should fail loudly, not silently create a broken account. Every constructor here validates its arguments and throws `IllegalArgumentException` for bad input. That's not pedantic — it's the only moment a class can guarantee its invariants.
3. **Returning mutable internal state.** `public List<String> getTransactions() { return transactions; }` lets callers `clear()` the account's history. Every getter that returns a collection returns `List.copyOf(...)` or similar. Cost: one object allocation per call. Benefit: the object's invariants can never be violated from outside.

## GUI extension

Each problem README includes a **GUI extension** section describing what the same model should look like as a JavaFX window. The GUI code is being delivered as a separate batch after the console versions. The model class doesn't change between console and GUI — only the driver does.
