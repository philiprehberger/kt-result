# kt-result

[![CI](https://github.com/philiprehberger/kt-result/actions/workflows/ci.yml/badge.svg)](https://github.com/philiprehberger/kt-result/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/result)](https://central.sonatype.com/artifact/com.philiprehberger/result)

A lightweight, typed Result monad for Kotlin with railway-oriented error handling.

## Requirements

- Kotlin 1.9+ / Java 17+

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.philiprehberger:result:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.philiprehberger:result:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>result</artifactId>
    <version>0.1.0</version>
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

## Development

```bash
./gradlew test       # Run tests
./gradlew check      # Run all checks
./gradlew build      # Build JAR
```

## License

MIT
