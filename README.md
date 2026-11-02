<!-- ============================================================== -->
<!-- Header — title, subtitle, call-to-action chips, metadata chips -->
<!-- ============================================================== -->

<h1 align="center">Chirp Examples</h1>

<p align="center">
  <em>Ten levels of Java problems, each solved twice — once in plain Java, once with <a href="https://github.com/hyphenSaad/chirp">Chirp</a>.</em>
</p>

<p align="center">
  <a href="https://github.com/hyphenSaad/chirp">
    <img alt="Library" src="https://img.shields.io/badge/library-chirp-e11d48?style=for-the-badge&logo=java&logoColor=white">
  </a>
  <a href="https://hyphensaad.github.io/chirp/">
    <img alt="Docs" src="https://img.shields.io/badge/docs-api-6366f1?style=for-the-badge&logo=read-the-docs&logoColor=white">
  </a>
  <a href="https://github.com/hyphenSaad/chirp/issues">
    <img alt="Issues" src="https://img.shields.io/badge/issues-open-22c55e?style=for-the-badge&logo=github&logoColor=white">
  </a>
</p>

<p align="center">
  <img alt="Java 17" src="https://img.shields.io/badge/java-17-orange?style=flat-square&logo=openjdk&logoColor=white">
  <img alt="License MIT" src="https://img.shields.io/badge/license-MIT-22c55e?style=flat-square">
  <img alt="Levels 8" src="https://img.shields.io/badge/levels-8-8b5cf6?style=flat-square">
  <img alt="Examples 24" src="https://img.shields.io/badge/problems-24-0ea5e9?style=flat-square">
  <img alt="Java files 80+" src="https://img.shields.io/badge/java%20files-80%2B-f59e0b?style=flat-square">
  <img alt="Chirp 1.0.0" src="https://img.shields.io/badge/chirp-1.0.0-e11d48?style=flat-square">
</p>

---

<!-- ============================================================== -->
<!-- What this repo is -->
<!-- ============================================================== -->

Every problem is **solved twice**:

- **`with-chirp/`** — using Chirp's shortcuts
- **`without-chirp/`** — using plain Java

Both produce **identical output** for identical input. Read them side by side. The point is not that Chirp makes the impossible possible; the point is that it makes the routine invisible, so the student's attention goes to the actual problem.

Levels 1–7 are console-only. Level 8 introduces JavaFX, where every problem also has **`with-chirp-gui/`** and **`without-chirp-gui/`** variants — the same logic, running in a window instead of a terminal.

---

<!-- ============================================================== -->
<!-- Learning path -->
<!-- ============================================================== -->

## The learning path

| # | Level | Theme | What you practice | Problems |
|:-:|:------|:------|:------------------|:--------:|
| 1 | [**First Steps**](01-first-steps/) | Input, output, formatting | Strings, integers, decimals, menus, `if`/`else` | 3 |
| 2 | [**Making Decisions**](02-making-decisions/) | Conditionals and validation | Chained ranges, independent modifiers, `switch` | 3 |
| 3 | [**Loops & Iteration**](03-loops-and-iteration/) | Repetition | Nested `for`, `while` + early exit, accumulators | 3 |
| 4 | [**Strings & Collections**](04-strings-and-collections/) | Data that grows | `String`, `List`, `Map`, grouping, filtering, sorting | 3 |
| 5 | [**Building Classes**](05-building-classes/) | OOP fundamentals | Encapsulation, constructors, invariants, state machines | 3 |
| 6 | [**Files & Persistence**](06-files-and-persistence/) | Data between runs | CSV, JSON, append-only logs, first-run handling | 3 |
| 7 | [**Talking to the World**](07-talking-to-the-world/) | Network requests | HTTP, JSON APIs, error handling, retries, email | 3 |
| 8 | [**Windows & Widgets**](08-windows-and-widgets/) | JavaFX basics | Layouts, controls, events, live updates | 3 |

Each problem ships a **full specification** in its own `README.md` — every requirement, every edge case, and a side-by-side comparison table showing what changed between the two implementations.

---

<!-- ============================================================== -->
<!-- Structure -->
<!-- ============================================================== -->

## Structure

```
01-first-steps/
├── README.md                 ← what this level covers
├── 01-hello-greeter/
│   ├── README.md             ← problem statement, examples, edge cases
│   ├── with-chirp/           ← Java, no build files
│   │   └── HelloGreeter.java
│   └── without-chirp/
│       └── HelloGreeter.java
└── 02-temperature-converter/
    ├── README.md
    ├── with-chirp/
    ├── without-chirp/
    ├── with-chirp-gui/       ← only at Level 8
    └── without-chirp-gui/
```

Every `.java` file is a **complete single-file program**. There are no build files, no folder-level dependencies, no shared code to configure. Copy any one file into a folder and it compiles and runs.

---

<!-- ============================================================== -->
<!-- Prerequisites -->
<!-- ============================================================== -->

## Prerequisites

- **Java 17** or later
- **Chirp 1.0.0** resolvable by Maven, for the `with-chirp/` and `with-chirp-gui/` variants
- **JavaFX 21** or later, for the `without-chirp-gui/` variants — or use the Chirp fat jar (which bundles JavaFX) for both GUI variants

**General setup** — downloading the jar, IDE configuration, JavaFX requirements — is documented in the library repo:

<p align="center">
  <a href="https://github.com/hyphenSaad/chirp#installation">
    <img alt="Installation guide" src="https://img.shields.io/badge/installation-guide-6366f1?style=for-the-badge&logo=read-the-docs&logoColor=white">
  </a>
</p>

**One step the general guide doesn't cover.** The examples resolve Chirp through Maven, so the artifact must exist in your local `~/.m2` repository. Install it once:

```bash
git clone https://github.com/hyphenSaad/chirp.git
cd chirp
mvn install
```

That's a one-time step per machine. If you'd rather not clone, download `chirp-1.0.0.jar` from the [latest release](https://github.com/hyphenSaad/chirp/releases/latest) and register it manually:

```bash
mvn install:install-file \
    -Dfile=chirp-1.0.0.jar \
    -DgroupId=io.github.hyphensaad \
    -DartifactId=chirp \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

Either command registers `io.github.hyphensaad:chirp:1.0.0` in `~/.m2`. After that, every `with-chirp/` example finds it automatically.

**You don't need Chirp to try the "without" versions.** Every `without-chirp/` and `without-chirp-gui/` file has zero dependencies beyond the JDK (and JavaFX for the GUI ones). Clone this repo, open any `without-chirp/` folder, and run the file.

---

<!-- ============================================================== -->
<!-- Reading order -->
<!-- ============================================================== -->

## Reading order

1. **The level README** — what the level covers and why that order
2. **The problem README** — the full specification, examples, and edge cases
3. **The two Java files** — side by side, `with-chirp` next to `without-chirp`

The problem README is a **specification**, not a hint. It says exactly what the program does, including every validation rule, every boundary value, and every error message. The two implementations are both correct — they just get there differently.

The `with-chirp` version is often shorter. That is not the point. The `without-chirp` version is what the student would write if they had never seen a library. Understanding *why* it is longer is what makes a library worth using.

---

<!-- ============================================================== -->
<!-- What Chirp replaces -->
<!-- ============================================================== -->

## What Chirp actually replaces

| Levels | Category | What Chirp handles | What you still write |
|:-------|:---------|:-------------------|:---------------------|
| **1–3** | Input plumbing | Validation loops, retry-on-invalid, menus, formatting | The actual logic — the conditions, loops, and decisions |
| **4–5** | Data operations | `Filter.where`, `Filter.groupBy`, `Text.*`, `Numbers.*` | The algorithm — the grouping key, the filter predicate |
| **6** | File handling | Reading, writing, parsing, first-run handling, error recovery | The persistence model — what to save and when |
| **7** | HTTP + SMTP | Request building, JSON parsing, timeout handling, retry logic | The API contract — which endpoint, which fields to read |
| **8** | Layout + styling | Control construction, layout helpers, variant styles, event wiring | The state model — what a button click means |

Every problem README has a table that makes the specific savings concrete for that problem. No example claims that Chirp does the thinking — it only does the typing.

---

<!-- ============================================================== -->
<!-- Contributing -->
<!-- ============================================================== -->

## Contributing

Any new example follows the same shape:

1. **Pick the right level** (`01-*` through `08-*`)
2. **Create a `NN-problem-name/` folder** with a `README.md` — requirements, edge cases, side-by-side table
3. **Add `with-chirp/` and `without-chirp/` subfolders** — each with one `.java` file
4. **Both versions must produce identical output** for the same input
5. **If the problem demonstrates a GUI**, add `with-chirp-gui/` and `without-chirp-gui/`

The problem statement is a **specification**, not a hint — it should say exactly what the program does, including every validation rule, every boundary value, and every error message.

Before opening a PR, sanity-check:

```bash
# Every with-chirp file should import chirp
grep -rn "import.*chirp" --include="*.java" */*/with-chirp/

# Every without-chirp file should NOT import chirp
grep -rn "import.*chirp" --include="*.java" */*/without-chirp/   # should produce no output
```

If any `without-chirp` file imports Chirp, the comparison is invalidated. Fix it before submitting.

---

<!-- ============================================================== -->
<!-- Related -->
<!-- ============================================================== -->

## Related

<p>
  <a href="https://github.com/hyphenSaad/chirp">
    <img alt="Chirp" src="https://img.shields.io/badge/github-chirp-181717?style=for-the-badge&logo=github&logoColor=white">
  </a>
  <a href="https://hyphensaad.github.io/chirp/">
    <img alt="API docs" src="https://img.shields.io/badge/api%20docs-javadoc-6366f1?style=for-the-badge&logo=read-the-docs&logoColor=white">
  </a>
  <a href="https://github.com/hyphenSaad/chirp/issues">
    <img alt="Issues" src="https://img.shields.io/badge/issues-report-e11d48?style=for-the-badge&logo=github&logoColor=white">
  </a>
</p>

- **[Chirp](https://github.com/hyphenSaad/chirp)** — the library these examples use, with the full installation guide
- **[API documentation](https://hyphensaad.github.io/chirp/)** — full Javadoc for every class and method
- **[Issue tracker](https://github.com/hyphenSaad/chirp/issues)** — bugs, feature requests, and questions

---

<!-- ============================================================== -->
<!-- License -->
<!-- ============================================================== -->

## License

MIT. See [LICENSE](LICENSE).