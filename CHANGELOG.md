# Changelog

## 0.1.4 (2026-03-22)

- Fix README compliance (badge label, installation format, remove Groovy section), standardize CHANGELOG

## 0.1.3 (2026-03-20)

- Standardize README: fix title, badges, version sync, remove Requirements section

## 0.1.2 (2026-03-18)

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## 0.1.1 (2026-03-18)

- Fix CI badge and gradlew permissions

## 0.1.0 (2026-03-17)

- `Result<T, E>` sealed interface with `Ok` and `Err` variants
- Extension functions: `map`, `flatMap`, `mapErr`, `recover`, `getOrElse`, `getOrThrow`
- `fold`, `onSuccess`, `onFailure` for pattern matching and side effects
- `resultOf { }` builder for catching exceptions into Result
- `zip` for combining up to 5 results
- `isOk` and `isErr` properties
