# Grade Book

A small grade book with pre-loaded student scores. Print three reports (per-student, per-subject, leaderboard) and allow searching by student name.

## Requirements

1. The program starts with this **pre-loaded** data — do not ask the user to enter it:

   ```
   Alice    Math       85
   Alice    Physics    78
   Alice    Chemistry  92
   Bob      Math       90
   Bob      Physics    88
   Bob      Chemistry  76
   Charlie  Math       70
   Charlie  Physics    65
   Diana    Math       95
   Diana    Physics    92
   Diana    Chemistry  88
   Eve      Math       60
   Eve      Physics    72
   Eve      Chemistry  68
   ```

   Note that **Charlie only has two subjects** — not everyone takes every subject. Do not assume every student has the same number of grades.

2. Print three tables, each preceded by a title line:

   **Table 1 — Per student (sorted alphabetically by name)**
   Columns: `Name`, `Subjects`, `Average` (average to two decimal places)

   **Table 2 — Per subject (sorted alphabetically by subject)**
   Columns: `Subject`, `Students`, `Average`

   **Table 3 — Leaderboard (sorted by average, descending)**
   Columns: `Rank`, `Name`, `Average`

3. After the tables, print one summary line:
   `"ℹ Top student: {name} ({average})"`
   using the top entry from the leaderboard.

4. Ask for a **student name search query** (case-insensitive substring). Print all matching **grades** in a table with columns `Student`, `Subject`, `Score`. If no matches, print `"ℹ No matches."`

## Expected output (based on the pre-loaded data above)

```
========================================
│ Grade Book
========================================

Per student:

Name     Subjects  Average
-------  --------  -------
Alice    3         85.00
Bob      3         84.67
Charlie  2         67.50
Diana    3         91.67
Eve      3         66.67

Per subject:

Subject    Students  Average
---------  --------  -------
Chemistry  4         81.00
Math       5         80.00
Physics    5         79.00

Leaderboard:

Rank  Name     Average
----  -------  -------
1     Diana    91.67
2     Alice    85.00
3     Bob      84.67
4     Charlie  67.50
5     Eve      66.67

ℹ Top student: Diana (91.67)

Search student: al

Student  Subject  Score
-------  -------  -----
Alice    Math     85
Alice    Physics  78
Alice    Chemistry 92
```

## Boundary cases you must check

| Case | Expected behaviour |
|---|---|
| Per-student averages | Charlie has 2 subjects, not 3 — his average is `(70+65)/2 = 67.50`, not `(70+65)/3` |
| Per-subject averages | Chemistry has 4 students, Math and Physics have 5 — the average divides by the *actual* count |
| Leaderboard order | Sorted strictly by average descending — Diana's 91.67 first |
| Search `"al"` | Matches Alice only |
| Search `"a"` | Matches Alice, Diana, Charlie, and also any student whose name contains `a` at any position — expect 3 or 4 depending on case |
| Search `"xyz"` | `"No matches."` |
| Search `""` | Every student matches (substring empty) |

## The `groupBy` + `average` pattern

Two operations appear three times in this problem, each time on a different key:

1. **Group the grades by student** → `Map<String, List<Grade>>`
2. **For each group, compute the average score** → `Map<String, Double>`
3. **Build a table row from the group** → `String[]{name, count, average}`

The same three steps apply for per-subject, just grouping by `grade.subject()` instead of `grade.student()`. Once you have the pattern, the second report is a copy of the first with one field swapped.

```java
Map<String, List<Grade>> byStudent = Filter.groupBy(grades, Grade::student);
for (var entry : byStudent.entrySet()) {
    List<Grade> studentGrades = entry.getValue();
    double avg = studentGrades.stream().mapToInt(Grade::score).average().orElse(0);
    rows.add(new String[]{
            entry.getKey(),
            String.valueOf(studentGrades.size()),
            decimal(avg, 2)
    });
}
```

The only subtlety is `orElse(0)` — `.average()` returns `OptionalDouble` because an empty stream has no average. If a student somehow has zero grades (impossible in the fixed data, but possible in code), you get `0.0` instead of an exception.

## Sorting by average vs sorting alphabetically

Per-student table is sorted **alphabetically** — `rows.sort(Comparator.comparing(row -> row[0]))`. Leaderboard is sorted **by average descending** — `rows.sort(Comparator.comparing(row -> Double.parseDouble(row[2]), Comparator.reverseOrder()))`.

Two different comparators, applied to the same data. This is the pattern you'll reuse in every "top N" problem. The data doesn't change; only the sort key does.

## What you'll practice

- **`groupingBy`** — restructuring a flat list into a map of groups
- **Aggregating within groups** — count, sum, average
- **Building report tables** — `List<String[]>` populated from aggregation results
- **Sorting by different keys** — alphabetical in one table, numeric descending in another
- **String search** — case-insensitive substring over a list of records
- **Working with records** — a `record Grade(String student, String subject, int score)` gives you named, typed accessors

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 116 | 154 |
| Group by student | `Filter.groupBy(grades, Grade::student)` | `grades.stream().collect(Collectors.groupingBy(Grade::student))` |
| Group by subject | `Filter.groupBy(grades, Grade::subject)` | `.stream().collect(Collectors.groupingBy(Grade::subject))` |
| Average | `grades.stream().mapToInt(Grade::score).average().orElse(0)` | same |
| Two-decimal format | `decimal(avg, 2)` | `String.format("%.2f", avg)` |
| Table render (×4) | `table(rows, headers)` | hand-rolled `printTable` ×4 |
| Search | `filter(grades, g -> contains(g.student(), query))` | `.stream().filter(g -> g.student().toLowerCase().contains(query.toLowerCase())).toList()` |

The aggregation logic is essentially the same in both. The savings are: one `Filter.groupBy` call instead of a `Collectors.groupingBy` (slightly more readable), `Search.contains` handling the case-insensitivity, and — the biggest win — `table(...)` called four times without any helper setup.
