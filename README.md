# result

[![Tests](https://github.com/philiprehberger/kt-result/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-result/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/result.svg)](https://central.sonatype.com/artifact/com.philiprehberger/result)
[![Last updated](https://img.shields.io/github/last-commit/philiprehberger/kt-result)](https://github.com/philiprehberger/kt-result/commits/main)

A lightweight, typed Result monad for Kotlin with railway-oriented error handling.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("com.philiprehberger:result:0.2.0")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>result</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Usage

```kotlin
import com.philiprehberger.result.*

// Create results
val ok: Result<Int, String> = Result.Ok(42)
val err: Result<Int, String> = Result.Err("not found")

// Map and chain
val doubled = ok.map { it * 2 }              // Ok(84)
val chained = ok.flatMap { Result.Ok(it + 1) } // Ok(43)
```

### Railway-Oriented Error Handling

```kotlin
fun parseAge(input: String): Result<Int, String> =
    resultOf { input.toInt() }.mapErr { "Invalid age: $input" }

fun validateAge(age: Int): Result<Int, String> =
    if (age in 0..150) Result.Ok(age) else Result.Err("Age out of range: $age")

val result = parseAge("25")
    .flatMap { validateAge(it) }
    .map { "Age is $it" }
    .getOrElse { "Error: $it" }
```

### Combining Results with Zip

```kotlin
val name: Result<String, String> = Result.Ok("Alice")
val age: Result<Int, String> = Result.Ok(30)

val combined = name.zip(age) { n, a -> "$n is $a years old" }
// Ok("Alice is 30 years old")
```

### Exception Catching

```kotlin
val result = resultOf { riskyOperation() }
result
    .onSuccess { println("Got: $it") }
    .onFailure { println("Failed: ${it.message}") }
```

### Filtering Results

```kotlin
fun validateAge(age: Int): Result<Int, String> =
    Result.Ok(age).filter({ "Age must be positive" }) { it > 0 }

validateAge(25)  // Ok(25)
validateAge(-1)  // Err("Age must be positive")
```

### Transforming Both Sides

```kotlin
val result: Result<Int, String> = Result.Ok(42)

// bimap transforms both Ok and Err sides
val mapped = result.bimap({ it.toString() }, { Exception(it) })
// Ok("42")

// swap exchanges Ok and Err
val swapped = result.swap()  // Err(42)
```

### Converting and Merging

```kotlin
val ok: Result<Int, String> = Result.Ok(42)
ok.toList()     // [42]
ok.getOrNull()  // 42

val err: Result<Int, String> = Result.Err("error")
err.toList()     // []
err.getOrNull()  // null

// merge extracts value when both types are the same
val r: Result<String, String> = Result.Ok("hello")
r.merge()  // "hello"
```

## API

| Function / Type | Description |
|-----------------|-------------|
| `Result<T, E>` | Sealed interface with `Ok<T>` and `Err<E>` variants |
| `map` | Transform the success value |
| `flatMap` | Chain results (monadic bind) |
| `mapErr` | Transform the error value |
| `recover` | Convert an error into a success |
| `getOrElse` | Extract value with a fallback |
| `getOrThrow` | Extract value or throw |
| `fold` | Handle both branches |
| `onSuccess` / `onFailure` | Side-effect callbacks |
| `resultOf { }` | Catch exceptions into Result |
| `zip` | Combine 2-5 results |
| `filter` | Keep Ok if predicate passes, else convert to Err |
| `swap` | Exchange Ok and Err types |
| `bimap` | Transform both success and error values |
| `toList` | Convert to single-element or empty list |
| `merge` | Extract value when both types match |
| `getOrNull` | Get success value or null |

## Development

```bash
./gradlew test       # Run tests
./gradlew check      # Run all checks
./gradlew build      # Build JAR
```

## Support

If you find this project useful:

⭐ [Star the repo](https://github.com/philiprehberger/kt-result)

🐛 [Report issues](https://github.com/philiprehberger/kt-result/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

💡 [Suggest features](https://github.com/philiprehberger/kt-result/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)

❤️ [Sponsor development](https://github.com/sponsors/philiprehberger)

🌐 [All Open Source Projects](https://philiprehberger.com/open-source-packages)

💻 [GitHub Profile](https://github.com/philiprehberger)

🔗 [LinkedIn Profile](https://www.linkedin.com/in/philiprehberger)

## License

[MIT](LICENSE)
