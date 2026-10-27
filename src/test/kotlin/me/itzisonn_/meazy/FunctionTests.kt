package me.itzisonn_.meazy

import me.itzisonn_.meazy.helper.TestingHelper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object FunctionTests {
    @BeforeAll
    @JvmStatic
    fun setup() {
        TestingHelper.initialize()
    }

    @Test
    fun test_basic() {
        val output = TestingHelper.loadAndRun("function/basic")
        assertEquals("Ok\n", output)
    }

    @Test
    fun test_parameter() {
        val output = TestingHelper.loadAndRun("function/parameter")
        assertEquals("Hi, Bob\n", output)
    }

    @Test
    fun test_multiple_parameters() {
        val output = TestingHelper.loadAndRun("function/multiple_parameters")
        assertEquals("Hello, Joe, which is 87 years old\n", output)
    }

    @Test
    fun test_shadowed_parameter() {
        val output = TestingHelper.loadAndRun("function/shadowed_parameter")
        assertEquals("Hey, BOB!\n", output)
    }

    @Test
    fun test_return_value() {
        val output = TestingHelper.loadAndRun("function/return_value")
        assertEquals("67\n", output)
    }

    @Test
    fun test_return_value_short() {
        val output = TestingHelper.loadAndRun("function/return_value_short")
        assertEquals("52\n", output)
    }

    @Test
    fun test_return_value_and_parameter() {
        val output = TestingHelper.loadAndRun("function/return_value_and_parameter")
        assertEquals("69\n", output)
    }

    @Test
    fun test_return_value_inside_not_returning() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/return_value_inside_not_returning")
        }
    }

    @Test
    fun test_return_without_value_inside_not_returning() {
        val output = TestingHelper.loadAndRun("function/return_without_value_inside_not_returning")
        assertEquals("But this would!\n", output)
    }

    @Test
    fun test_missing_return() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/missing_return")
        }
    }

    @Test
    fun test_return_with_missing_value() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/return_with_missing_value")
        }
    }

    @Test
    fun test_return_with_other_type() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/return_with_other_type")
        }
    }

    @Test
    fun test_missing_return_inside_if() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/missing_return_inside_if")
        }
    }

    @Test
    fun test_missing_return_outside_if() {
        val output = TestingHelper.loadAndRun("function/missing_return_outside_if")
        assertEquals("Oh wow, 1 is equal to 1!\n67\n", output)
    }

    @Test
    fun test_missing_return_inside_for() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/missing_return_inside_for")
        }
    }

    @Test
    fun test_missing_return_outside_for() {
        val output = TestingHelper.loadAndRun("function/missing_return_outside_for")
        assertEquals("Looping 1\nLooping 2\nLooping 3\n67\n", output)
    }

    @Test
    fun test_missing_return_inside_while() {
        assertFailsWith<IllegalStateException> {
            TestingHelper.loadAndRun("function/missing_return_inside_while")
        }
    }

    @Test
    fun test_missing_return_outside_while() {
        val output = TestingHelper.loadAndRun("function/missing_return_outside_while")
        assertEquals("Looping 0\nLooping 1\n67\n", output)
    }
}