# Shopping Cart

Build a `ShoppingCart` class that holds a list of `Item` records, allows adding/removing items, applies a percentage discount, and computes a total. Drive it from a menu.

## Requirements — the classes

Design two types:

**`Item` — an immutable record:**

```java
record Item(String name, double price, int quantity) {
    double lineTotal() { return price * quantity; }
}
```

**`ShoppingCart` — a mutable container:**

1. **Private fields** — `List<Item> items`, `double discountPercent`. No public setters.
2. **`addItem(String name, double price, int quantity)`**
   - Reject `null`/blank name, `price <= 0`, `quantity <= 0` by throwing `IllegalArgumentException`
   - If the item already exists (same name), **increase its quantity** rather than adding a duplicate
3. **`removeItem(String name)`** — returns `boolean`
   - `false` if no item with that name exists
   - `true` otherwise (remove the item entirely — quantities are not decremented)
4. **`applyDiscount(double percent)`**
   - Reject `percent < 0` or `percent > 100` by throwing `IllegalArgumentException`
   - `0` clears any existing discount
5. **Accessors:**
   - `items()` — returns an immutable copy of the list
   - `subtotal()` — sum of every item's `lineTotal()`
   - `discountAmount()` — `subtotal() × discountPercent / 100`
   - `total()` — `subtotal() - discountAmount()`
   - `itemCount()` — sum of every item's `quantity` (not number of distinct items)

## Requirements — the demo

1. Create an empty `ShoppingCart`.
2. Repeatedly show a menu:
   ```
   Action:
     1) Add item
     2) Remove item
     3) Apply discount
     4) View cart
     5) Checkout
   ```
3. **Add item** — prompt for name, price, and quantity (all validated), call `addItem()`, print `"✓ Added N × Name @ Rs.P"`
4. **Remove item** — prompt for name, call `removeItem()`, print success or `"✗ No such item in cart."`
5. **Apply discount** — prompt for percent (0 to 100), call `applyDiscount()`, print `"✓ Discount set to N%"`
6. **View cart** — if empty, print `"ℹ Cart is empty."`. Otherwise print a table with columns `Item`, `Price`, `Qty`, `Total`, then `subtotal`, `discount`, and `total`:
   ```
   Item         Price      Qty  Total
   -----------  ---------  ---  ----------
   Notebook     Rs.150.00  2    Rs.300.00
   Pen          Rs.25.00   10   Rs.250.00
   
   Subtotal:  Rs.550.00
   Discount:  Rs.55.00  (10%)
   Total:     Rs.495.00
   ```
7. **Checkout** — if cart is empty, print `"ℹ Nothing to check out."` and return to menu. Otherwise print the same summary as View cart, add `"✓ Checked out — thank you!"` and exit.

## Example interaction

```
========================================
│ Shopping Cart
========================================
Action:
  1) Add item
  2) Remove item
  3) Apply discount
  4) View cart
  5) Checkout
Your choice [1-5]: 1
Item name: Notebook
Price: 150
Quantity: 2

✓ Added 2 × Notebook @ Rs.150.00

Action:
  1) Add item
  2) Remove item
  3) Apply discount
  4) View cart
  5) Checkout
Your choice [1-5]: 1
Item name: Notebook
Price: 150
Quantity: 3

✓ Added 3 × Notebook @ Rs.150.00

Action: ...
Your choice [1-5]: 4

Item      Price      Qty  Total
--------  ---------  ---  ----------
Notebook  Rs.150.00  5    Rs.750.00

Subtotal:  Rs.750.00
Discount:  Rs.0.00  (0%)
Total:     Rs.750.00
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Add same item twice | Quantities merge — one row in cart, total quantity adds |
| Add with `quantity = 0` | `IllegalArgumentException` |
| Add with `price = -1` | `IllegalArgumentException` |
| Remove non-existent item | Returns `false`, cart unchanged |
| Remove the last remaining item | Cart becomes empty, `itemCount()` returns 0 |
| Discount `0` | Clears any existing discount |
| Discount `-5` | `IllegalArgumentException` |
| Discount `101` | `IllegalArgumentException` |
| Discount `10` on subtotal `500` | `discountAmount() = 50`, `total() = 450` |
| Checkout on empty cart | Prints `"Nothing to check out"`, does not exit |
| `itemCount()` with `2 × A + 3 × B` | Returns `5`, not `2` |

## The "merge on duplicate add" pattern

When the user adds the same item twice, the cart **does not contain two rows for it**. The two `addItem` calls combine into a single `Item` with the summed quantity.

Two reasons:
1. **Ordering is stable** — the item stays in its original position, so the cart doesn't jitter on repeated adds
2. **Discounts and totals see a single line** — one "Notebook × 5" row instead of five "Notebook × 1" rows

Implementation: when `addItem` is called with an existing name, replace the existing `Item` with a new one that has the combined quantity. Since `Item` is a **record**, you can't mutate `quantity` — you construct a new one. That's a feature, not a bug: `record` immutability means the cart's "merge" logic is explicit (replace), not accidental (mutate).

## Why `Item` is a record but `ShoppingCart` is a class

- `Item` is a **value** — two `Item`s with the same name, price, and quantity are interchangeable. The record's auto-generated `equals`/`hashCode` reflect that. It has no mutable state; changing quantity means making a new one.
- `ShoppingCart` is an **entity** — it has identity. Two empty carts are not the same cart. It has mutable state (`items`, `discountPercent`), and its methods change that state. A class with private fields is the right tool.

That's the general rule: **records for values, classes for entities with behaviour**. Getting this distinction right early saves a lot of confusion later, when the student has to decide whether a `User` should be a record (usually not — it has mutable state) or a `Point` should be a class (usually yes — it's a value).

## GUI extension

Build a JavaFX window that:
- Shows a `TableView` with columns `Item`, `Price`, `Qty`, `Total`
- Below the table: `Subtotal`, `Discount` (with a `TextField` + Apply button), `Total` as a large heading
- Has an Add-Item button that opens a small dialog (or inline fields)
- Has a Checkout button that shows a confirmation dialog

Same `ShoppingCart` model, different driver.

## What you'll practice

- The record vs class distinction (`Item` vs `ShoppingCart`)
- Merging on duplicate add (immutability via `record` + explicit replace)
- Multi-parameter validation in `addItem` and `applyDiscount`
- Immutable accessor (`items()` returns `List.copyOf`)
- Formatted cart table with subtotal/discount/total

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 138 | 270 |
| Model (`ShoppingCart` + `Item`) | **identical** — pure Java | **identical** |
| Menu selection | `readChoice("Action:", ...)` | hand-rolled menu loop |
| Item name input | `readNonEmpty("Item name")` | hand-rolled `while` + `trim().isEmpty()` |
| Price input (min) | `readDouble("Price").min(0.01).read()` | hand-rolled `while` + `try`/`catch` |
| Quantity input (min) | `readInt("Quantity", 1, Integer.MAX_VALUE)` | hand-rolled `while` + `try`/`catch` |
| Discount input (range) | `readDouble("Percent").min(0).max(100).read()` | hand-rolled `while` + `try`/`catch` |
| Cart table | `table(rows, "Item", "Price", "Qty", "Total")` | hand-rolled `printTable` helper |
| Currency | `rupees(amount)` | `"Rs." + String.format("%,.2f", amount)` |
