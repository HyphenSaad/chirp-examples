# Weather Lookup

Fetch current weather for a chosen city from a public HTTP API, parse the nested JSON response, and display a formatted report.

## Requirements

1. Offer a menu of exactly four cities. For each city, use these coordinates:
   - **Lahore** — latitude `31.55`, longitude `74.34`
   - **Karachi** — latitude `24.86`, longitude `67.01`
   - **Islamabad** — latitude `33.69`, longitude `73.04`
   - **Peshawar** — latitude `34.01`, longitude `71.58`
2. Fetch current weather from **`https://api.open-meteo.com/v1/forecast`** with these query parameters:
   - `latitude` — the chosen latitude
   - `longitude` — the chosen longitude
   - `current_weather` — literal value `true`
3. The response has the shape (fields not needed are omitted):
   ```json
   {
     "current_weather": {
       "temperature": 25.4,
       "windspeed": 10.8,
       "weathercode": 0
     }
   }
   ```
4. Print a report like this:
   ```
   ========================================
   │ Weather — Lahore
   ========================================
   ℹ 25.4 °C
   ℹ Clear sky
   ℹ Wind 10.8 km/h
   ℹ Fetched in 412 ms
   ```
5. Map `weathercode` to a human-readable description using this table:

   | Code | Description |
   |---|---|
   | 0 | Clear sky |
   | 1, 2, 3 | Mainly clear / partly cloudy / overcast |
   | 45, 48 | Fog |
   | 51, 53, 55 | Drizzle |
   | 61, 63, 65 | Rain |
   | 71, 73, 75 | Snow |
   | 80, 81, 82 | Rain showers |
   | 95 | Thunderstorm |
   | (any other code) | Unknown |

6. After the report, show a menu:
   ```
   Action:
     1) Look up another city
     2) Exit
   ```
7. **On network failure** — timeout, DNS failure, non-2xx status, or missing `current_weather` in the response — print `"⚠ Could not fetch weather for {city}."` and do **not** print the weather report. The menu still appears.

## Example interaction

```
========================================
│ Weather Lookup
========================================
Pick a city:
  1) Lahore
  2) Karachi
  3) Islamabad
  4) Peshawar
Your choice [1-4]: 1

========================================
│ Weather — Lahore
========================================
ℹ 25.4 °C
ℹ Clear sky
ℹ Wind 10.8 km/h
ℹ Fetched in 412 ms

Action:
  1) Look up another city
  2) Exit
Your choice [1-2]: 2

ℹ Goodbye.
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Normal weather response | Temperature, description, wind displayed |
| `weathercode = 0` | `"Clear sky"` |
| `weathercode = 2` | `"Mainly clear / partly cloudy / overcast"` (or a shorter phrase, your choice) |
| `weathercode = 61` | `"Rain"` |
| `weathercode = 99` (not in table) | `"Unknown"` |
| Response missing `current_weather` | Treated as failure — no report |
| Response has `current_weather` but missing `temperature` | Treated as failure |
| Network down | Failure message for that city, then menu returns |
| City picked and looked up twice in a row | Both requests succeed, may report different times |

## The query parameter lesson

The API URL is not hardcoded — the latitude and longitude are different per city, and they have to be encoded into the URL query string:

```
https://api.open-meteo.com/v1/forecast?latitude=31.55&longitude=74.34&current_weather=true
```

The `with-chirp` version builds this with `Http.get(url).query("latitude", ...).query("longitude", ...).query(...)`. Each `.query(...)` call URL-encodes its value and appends it to the query string. If the value were `"Lahore, Pakistan"` (a city name rather than coordinates), the comma and space would need encoding — Chirp's `.query()` handles that automatically.

The `without-chirp` version builds the URL with string concatenation:

```java
String url = BASE_URL + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current_weather=true";
```

That works for numeric coordinates (they contain no special characters), but it would silently break if a parameter needed URL-encoding. That's the trade-off.

## The "map codes to phrases" pattern

The weather API returns a numeric code. The demo turns it into a phrase via a `switch`:

```java
private static String describe(int code) {
    return switch (code) {
        case 0 -> "Clear sky";
        case 1, 2, 3 -> "Mainly clear / partly cloudy / overcast";
        case 45, 48 -> "Fog";
        case 51, 53, 55 -> "Drizzle";
        case 61, 63, 65 -> "Rain";
        case 71, 73, 75 -> "Snow";
        case 80, 81, 82 -> "Rain showers";
        case 95 -> "Thunderstorm";
        default -> "Unknown";
    };
}
```

Three properties of this pattern worth noticing:

1. **Multiple cases per branch** — Java 17's enhanced `switch` allows `case 1, 2, 3 -> ...` — several values mapping to one outcome. Without that, you'd write 14 separate `case` branches for what is eight logical outcomes.
2. **The default is essential** — the API can add new weather codes at any time. The `default -> "Unknown"` guarantees the program still works with future codes. If you removed it, the compiler would warn about a non-exhaustive switch (and Java 17 would throw at runtime for an unmatched int).
3. **The mapping lives in one place** — if you want "Rain" to say "Rain (heavy, moderate, light)" depending on 61 vs 63 vs 65, that's a change in one function.

## GUI extension

Build a JavaFX window that:
- Has a `ComboBox<String>` for the city (Lahore / Karachi / Islamabad / Peshawar)
- Has a Lookup button that triggers a fetch
- Shows the temperature as a big heading (`Label.heading`)
- Shows the weather description as a subtitle
- Shows the wind speed below
- Uses a `ProgressIndicator` while the request is in flight

Same API, same JSON parsing, different driver — and this is where `Http.get(url).sendAsync()` combined with `Ui.runAsync` becomes essential (a synchronous call would freeze the window).

## What you'll practice

- URL query parameters — building a URL with `?key=value&key2=value2`
- Parsing **nested** JSON — `response.asObject().getObject("current_weather")`
- Mapping numeric codes to descriptions via `switch`
- Handling **partial** failures gracefully — a network failure for one city should not crash the loop
- Reporting timing as part of the output

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 81 | 190 |
| URL building | `Http.get(BASE).query("latitude", lat).query(...)` | manual `String.format` + `URLEncoder` (4 lines) |
| Request | `.send()` | `HttpRequest.newBuilder()` + `HttpClient.send()` (12 lines) |
| Parse nested JSON | `response.asObject().getObject("current_weather").getDouble("temperature")` | manual substring extraction at three levels of nesting (~40 lines) |
| Detect transport error | `if (response.hasTransportError())` | `try`/`catch IOException` + `catch InterruptedException` |
| Detect status error | `if (!response.isSuccess())` | `if (response.statusCode() < 200 \|\| response.statusCode() >= 300)` |
| Weather code mapping | same — `switch` | same |
| City list | `readChoice("Pick a city:", cities)` | hand-rolled menu loop |

The **nested JSON** parsing is the standout gap. Reading `current_weather.temperature` from a hand-parsed string requires three separate `extractString` calls, each one fragile. Chirp's `JsonObject` chains the traversal cleanly.
