package com.philiprehberger.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResultTest {

    @Test
    fun `Ok holds a value`() {
        val result: Result<Int, String> = Result.Ok(42)
        assertIs<Result.Ok<Int>>(result)
        assertEquals(42, result.value)
    }

    @Test
    fun `Err holds an error`() {
        val result: Result<Int, String> = Result.Err("failure")
        assertIs<Result.Err<String>>(result)
        assertEquals("failure", result.error)
    }

    @Test
    fun `isOk and isErr`() {
        val ok: Result<Int, String> = Result.Ok(1)
        val err: Result<Int, String> = Result.Err("e")
        assertTrue(ok.isOk)
        assertFalse(ok.isErr)
        assertFalse(err.isOk)
        assertTrue(err.isErr)
    }

    @Test
    fun `map transforms Ok value`() {
        val result = Result.Ok(10).map { it * 2 }
        assertEquals(Result.Ok(20), result)
    }

    @Test
    fun `map does not transform Err`() {
        val result: Result<Int, String> = Result.Err("error")
        val mapped = result.map { it * 2 }
        assertEquals(Result.Err("error"), mapped)
    }

    @Test
    fun `flatMap chains Ok values`() {
        val result = Result.Ok(5)
            .flatMap { Result.Ok(it + 1) }
            .flatMap { Result.Ok(it * 3) }
        assertEquals(Result.Ok(18), result)
    }

    @Test
    fun `flatMap short-circuits on Err`() {
        val result: Result<Int, String> = Result.Ok(5)
            .flatMap<Int, String, Int> { Result.Err("boom") }
            .flatMap { Result.Ok(it * 3) }
        assertEquals(Result.Err("boom"), result)
    }

    @Test
    fun `mapErr transforms error value`() {
        val result: Result<Int, String> = Result.Err("error")
        val mapped = result.mapErr { it.length }
        assertEquals(Result.Err(5), mapped)
    }

    @Test
    fun `recover produces Ok from Err`() {
        val result: Result<Int, String> = Result.Err("default")
        val recovered = result.recover { 0 }
        assertEquals(Result.Ok(0), recovered)
    }

    @Test
    fun `getOrElse returns value for Ok`() {
        val result: Result<Int, String> = Result.Ok(42)
        assertEquals(42, result.getOrElse { 0 })
    }

    @Test
    fun `getOrElse returns default for Err`() {
        val result: Result<Int, String> = Result.Err("missing")
        assertEquals(0, result.getOrElse { 0 })
    }

    @Test
    fun `getOrThrow returns value for Ok`() {
        val result: Result<Int, String> = Result.Ok(42)
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `getOrThrow throws for Err`() {
        val result: Result<Int, String> = Result.Err("bad")
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    @Test
    fun `fold applies correct branch`() {
        val ok: Result<Int, String> = Result.Ok(10)
        val err: Result<Int, String> = Result.Err("err")

        assertEquals("value=10", ok.fold({ "value=$it" }, { "error=$it" }))
        assertEquals("error=err", err.fold({ "value=$it" }, { "error=$it" }))
    }

    @Test
    fun `onSuccess runs action for Ok`() {
        var captured = 0
        Result.Ok(42).onSuccess { captured = it }
        assertEquals(42, captured)
    }

    @Test
    fun `onFailure runs action for Err`() {
        var captured = ""
        Result.Err("fail").onFailure { captured = it }
        assertEquals("fail", captured)
    }

    @Test
    fun `resultOf catches exception`() {
        val result = resultOf { error("boom") }
        assertIs<Result.Err<Throwable>>(result)
        assertEquals("boom", result.error.message)
    }

    @Test
    fun `resultOf wraps success`() {
        val result = resultOf { 42 }
        assertEquals(Result.Ok(42), result)
    }

    @Test
    fun `zip combines two Ok results`() {
        val a: Result<Int, String> = Result.Ok(1)
        val b: Result<Int, String> = Result.Ok(2)
        val combined = a.zip(b) { x, y -> x + y }
        assertEquals(Result.Ok(3), combined)
    }

    @Test
    fun `zip returns first Err`() {
        val a: Result<Int, String> = Result.Err("first")
        val b: Result<Int, String> = Result.Ok(2)
        val combined = a.zip(b) { x, y -> x + y }
        assertEquals(Result.Err("first"), combined)
    }

    @Test
    fun `zip combines five Ok results`() {
        val r1: Result<Int, String> = Result.Ok(1)
        val r2: Result<Int, String> = Result.Ok(2)
        val r3: Result<Int, String> = Result.Ok(3)
        val r4: Result<Int, String> = Result.Ok(4)
        val r5: Result<Int, String> = Result.Ok(5)
        val combined = zip(r1, r2, r3, r4, r5) { a, b, c, d, e -> a + b + c + d + e }
        assertEquals(Result.Ok(15), combined)
    }

    @Test
    fun `zip five returns first Err`() {
        val r1: Result<Int, String> = Result.Ok(1)
        val r2: Result<Int, String> = Result.Err("second")
        val r3: Result<Int, String> = Result.Ok(3)
        val r4: Result<Int, String> = Result.Err("fourth")
        val r5: Result<Int, String> = Result.Ok(5)
        val combined = zip(r1, r2, r3, r4, r5) { a, b, c, d, e -> a + b + c + d + e }
        assertEquals(Result.Err("second"), combined)
    }

    @Test
    fun `filter keeps Ok when predicate passes`() {
        val result: Result<Int, String> = Result.Ok(10)
        val filtered = result.filter({ "too small" }) { it > 5 }
        assertEquals(Result.Ok(10), filtered)
    }

    @Test
    fun `filter converts Ok to Err when predicate fails`() {
        val result: Result<Int, String> = Result.Ok(3)
        val filtered = result.filter({ "too small" }) { it > 5 }
        assertEquals(Result.Err("too small"), filtered)
    }

    @Test
    fun `filter passes through Err unchanged`() {
        val result: Result<Int, String> = Result.Err("original")
        val filtered = result.filter({ "too small" }) { it > 5 }
        assertEquals(Result.Err("original"), filtered)
    }

    @Test
    fun `swap converts Ok to Err`() {
        val result: Result<Int, String> = Result.Ok(42)
        val swapped = result.swap()
        assertEquals(Result.Err(42), swapped)
    }

    @Test
    fun `swap converts Err to Ok`() {
        val result: Result<Int, String> = Result.Err("error")
        val swapped = result.swap()
        assertEquals(Result.Ok("error"), swapped)
    }

    @Test
    fun `bimap transforms Ok side`() {
        val result: Result<Int, String> = Result.Ok(10)
        val mapped = result.bimap({ it * 2 }, { it.length })
        assertEquals(Result.Ok(20), mapped)
    }

    @Test
    fun `bimap transforms Err side`() {
        val result: Result<Int, String> = Result.Err("hello")
        val mapped = result.bimap({ it * 2 }, { it.length })
        assertEquals(Result.Err(5), mapped)
    }

    @Test
    fun `toList returns single-element list for Ok`() {
        val result: Result<Int, String> = Result.Ok(42)
        assertEquals(listOf(42), result.toList())
    }

    @Test
    fun `toList returns empty list for Err`() {
        val result: Result<Int, String> = Result.Err("error")
        assertEquals(emptyList(), result.toList())
    }

    @Test
    fun `merge returns value from Ok`() {
        val result: Result<String, String> = Result.Ok("success")
        assertEquals("success", result.merge())
    }

    @Test
    fun `merge returns error from Err`() {
        val result: Result<String, String> = Result.Err("failure")
        assertEquals("failure", result.merge())
    }

    @Test
    fun `getOrNull returns value for Ok`() {
        val result: Result<Int, String> = Result.Ok(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Err`() {
        val result: Result<Int, String> = Result.Err("error")
        assertEquals(null, result.getOrNull())
    }
}
