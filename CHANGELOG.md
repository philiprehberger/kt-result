# Changelog

All notable changes to this library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.1] - 2026-03-18

- Fix CI badge and gradlew permissions

## [0.1.0] - 2026-03-17

### Added
- `Result<T, E>` sealed interface with `Ok` and `Err` variants
- Extension functions: `map`, `flatMap`, `mapErr`, `recover`, `getOrElse`, `getOrThrow`
- `fold`, `onSuccess`, `onFailure` for pattern matching and side effects
- `resultOf { }` builder for catching exceptions into Result
- `zip` for combining up to 5 results
- `isOk` and `isErr` properties
