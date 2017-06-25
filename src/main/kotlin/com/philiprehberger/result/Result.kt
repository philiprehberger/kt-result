package com.philiprehberger.result

/**
 * A typed Result monad representing either a successful value [Ok] or an error [Err].
 *
 * Provides railway-oriented error handling with composable operations
 * like [map], [flatMap], [mapErr], [recover], and [fold].
 *
 * @param T the type of the success value
 * @param E the type of the error value
 */
public sealed interface Result<out T, out E> {

    /**
     * Represents a successful result containing a [value].
     *
     * @param T the type of the success value
     * @property value the success value
     */
    public data class Ok<out T>(public val value: T) : Result<T, Nothing>

    /**
     * Represents a failed result containing an [error].
     *
     * @param E the type of the error value
     * @property error the error value
     */
    public data class Err<out E>(public val error: E) : Result<Nothing, E>
}

/**
 * Returns `true` if this result is [Result.Ok].
 */
public val <T, E> Result<T, E>.isOk: Boolean
    get() = this is Result.Ok

/**
 * Returns `true` if this result is [Result.Err].
 */
public val <T, E> Result<T, E>.isErr: Boolean
    get() = this is Result.Err

/**
 * Transforms the success value using [transform], leaving errors unchanged.
 *
 * @param transform the function to apply to the success value
 * @return a new [Result] with the transformed success value, or the original error
 */
public inline fun <T, E, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> = when (this) {
    is Result.Ok -> Result.Ok(transform(value))
    is Result.Err -> this
}

/**
 * Transforms the success value using [transform] which itself returns a [Result],
 * flattening the nested result.
 *
 * @param transform the function to apply to the success value
 * @return the [Result] returned by [transform], or the original error
 */
public inline fun <T, E, R> Result<T, E>.flatMap(transform: (T) -> Result<R, E>): Result<R, E> = when (this) {
    is Result.Ok -> transform(value)
    is Result.Err -> this
}

/**
 * Transforms the error value using [transform], leaving success values unchanged.
 *
 * @param transform the function to apply to the error value
 * @return a new [Result] with the transformed error value, or the original success
 */
public inline fun <T, E, R> Result<T, E>.mapErr(transform: (E) -> R): Result<T, R> = when (this) {
    is Result.Ok -> this
    is Result.Err -> Result.Err(transform(error))
}

/**
 * Recovers from an error by applying [transform] to produce a success value.
 *
 * @param transform the function to apply to the error to produce a recovery value
 * @return [Result.Ok] with either the original value or the recovered value
 */
public inline fun <T, E> Result<T, E>.recover(transform: (E) -> T): Result.Ok<T> = when (this) {
    is Result.Ok -> this
    is Result.Err -> Result.Ok(transform(error))
}

/**
 * Returns the success value or the result of [default] if this is an error.
 *
 * @param default the function to compute a default value from the error
 * @return the success value or the computed default
 */
public inline fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T = when (this) {
    is Result.Ok -> value
    is Result.Err -> default(error)
}

/**
 * Returns the success value or throws [IllegalStateException] if this is an error.
 *
 * @return the success value
 * @throws IllegalStateException if this is an error
 */
public fun <T, E> Result<T, E>.getOrThrow(): T = when (this) {
    is Result.Ok -> value
    is Result.Err -> throw IllegalStateException("Called getOrThrow on Err: $error")
}

/**
 * Applies [onOk] if this is a success or [onErr] if this is an error, returning the result.
 *
 * @param onOk the function to apply to the success value
 * @param onErr the function to apply to the error value
 * @return the result of the applied function
 */
public inline fun <T, E, R> Result<T, E>.fold(onOk: (T) -> R, onErr: (E) -> R): R = when (this) {
    is Result.Ok -> onOk(value)
    is Result.Err -> onErr(error)
}

/**
 * Executes [action] if this is a success, returning the original result for chaining.
 *
 * @param action the action to perform with the success value
 * @return this result unchanged
 */
public inline fun <T, E> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Ok) action(value)
    return this
}

/**
 * Executes [action] if this is an error, returning the original result for chaining.
 *
 * @param action the action to perform with the error value
 * @return this result unchanged
 */
public inline fun <T, E> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Err) action(error)
    return this
}

/**
 * Executes [block] and wraps the return value in [Result.Ok].
 * If [block] throws an exception, it is caught and wrapped in [Result.Err].
 *
 * @param block the block to execute
 * @return [Result.Ok] with the block's return value, or [Result.Err] with the caught exception
 */
public inline fun <T> resultOf(block: () -> T): Result<T, Throwable> =
    try {
        Result.Ok(block())
    } catch (e: Throwable) {
        Result.Err(e)
    }

/**
 * Combines two [Result] values using [transform] if both are [Result.Ok].
 * Returns the first encountered error otherwise.
 *
 * @param other the second result to combine
 * @param transform the function to combine both success values
 * @return a new [Result] with the combined value, or the first error
 */
public inline fun <T1, T2, E, R> Result<T1, E>.zip(
    other: Result<T2, E>,
    transform: (T1, T2) -> R,
): Result<R, E> = flatMap { t1 -> other.map { t2 -> transform(t1, t2) } }

/**
 * Combines three [Result] values using [transform] if all are [Result.Ok].
 * Returns the first encountered error otherwise.
 */
public inline fun <T1, T2, T3, E, R> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    transform: (T1, T2, T3) -> R,
): Result<R, E> = r1.flatMap { t1 -> r2.flatMap { t2 -> r3.map { t3 -> transform(t1, t2, t3) } } }

/**
 * Combines four [Result] values using [transform] if all are [Result.Ok].
 * Returns the first encountered error otherwise.
 */
public inline fun <T1, T2, T3, T4, E, R> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    transform: (T1, T2, T3, T4) -> R,
): Result<R, E> = r1.flatMap { t1 -> r2.flatMap { t2 -> r3.flatMap { t3 -> r4.map { t4 -> transform(t1, t2, t3, t4) } } } }

/**
 * Combines five [Result] values using [transform] if all are [Result.Ok].
 * Returns the first encountered error otherwise.
 */
public inline fun <T1, T2, T3, T4, T5, E, R> zip(
    r1: Result<T1, E>,
    r2: Result<T2, E>,
    r3: Result<T3, E>,
    r4: Result<T4, E>,
    r5: Result<T5, E>,
    transform: (T1, T2, T3, T4, T5) -> R,
): Result<R, E> = r1.flatMap { t1 -> r2.flatMap { t2 -> r3.flatMap { t3 -> r4.flatMap { t4 -> r5.map { t5 -> transform(t1, t2, t3, t4, t5) } } } } }
