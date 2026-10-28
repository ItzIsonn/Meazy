@file:Suppress("KotlinMisorderedAssertEqualsArguments")

package me.itzisonn_.meazy

import me.itzisonn_.meazy.helper.TestingHelper
import me.itzisonn_.meazy.helper.asMultiline
import org.junit.jupiter.api.BeforeAll
import java.lang.reflect.InvocationTargetException
import kotlin.test.*

object ExpressionTests {
    @BeforeAll
    @JvmStatic
    fun setup() {
        TestingHelper.initialize()
    }

    @Test
    fun test_calc_add() {
        val output = TestingHelper.loadAndRun("expression/calc_add")
        assertEquals(asMultiline(3, 3, 1, -1, -1, 1, -3, -3), output)
    }

    @Test
    fun test_calc_sub() {
        val output = TestingHelper.loadAndRun("expression/calc_sub")
        assertEquals(asMultiline(-1, 1, -3, -3, 3, 3, 1, -1), output)
    }

    @Test
    fun test_calc_mul() {
        val output = TestingHelper.loadAndRun("expression/calc_mul")
        assertEquals(asMultiline(6, 6, -6, -6, -6, -6, 6, 6), output)
    }

    @Test
    fun test_calc_div() {
        val output = TestingHelper.loadAndRun("expression/calc_div")
        assertEquals(asMultiline(5, 5, 4, -5, -5, -4, -5, -5, -4, 5, 5, 4), output)
    }

    @Test
    fun test_calc_rem() {
        val output = TestingHelper.loadAndRun("expression/calc_rem")
        assertEquals(asMultiline(1, 0, 3, -1, 0, -3, 1, 0, 3, -1, 0, -3), output)
    }

    @Test
    fun test_calc_pow() {
        val output = TestingHelper.loadAndRun("expression/calc_pow")
        assertEquals(asMultiline(25f, 32f, 25f, -32f, 1/25f, 1/32f, 1/25f, -1/32f), output)
    }

    @Test
    fun test_order_of_math_operations() {
        val output = TestingHelper.loadAndRun("expression/order_of_math_operations")
        assertEquals(asMultiline(7, 7, 9, 8, 10, 10, 8, 2, 54f), output)
    }

    @Test
    fun test_order_of_logical_operations() {
        val output = TestingHelper.loadAndRun("expression/order_of_logical_operations")
        assertEquals(asMultiline(false, true, false, false, true, true, true, true, false, true, true, false, false, true, true, true), output)
    }

    @Test
    fun test_list_creation() {
        val output = TestingHelper.loadAndRun("expression/list_creation")
        assertEquals(asMultiline("1 2 3 4 ", "1.5 2.7 3.2 4.0 ", "true false true true ", "Hi! My name is John "), output)
    }

    @Test
    fun test_map_creation() {
        val output = TestingHelper.loadAndRun("expression/map_creation")
        assertEquals(asMultiline("0 = 0.0", "1 = true", "67 = 52", "5 = Wow!"), output)
    }

    @Test
    fun test_casting() {
        val output = TestingHelper.loadAndRun("expression/casting")
        assertEquals(asMultiline(7, "Why did I do this?"), output)
    }

    @Test
    fun test_casting_fail() {
        assertFailsWith<InvocationTargetException> {
            TestingHelper.loadAndRun("expression/casting_fail")
        }
    }

    @Test
    fun test_casting_safe() {
        val output = TestingHelper.loadAndRun("expression/casting_safe")
        assertEquals("null does work\n", output)
    }

    @Test
    fun test_type_check() {
        val output = TestingHelper.loadAndRun("expression/type_check")
        assertEquals(
            asMultiline(
                true, false, false, false, true, false, false, false, true, false, false, false,
                false, true, true, true, false, true, true, true, false, true, true, true
            ),
            output
        )
    }

    @Test
    fun test_null_check() {
        val output = TestingHelper.loadAndRun("expression/null_check")
        assertEquals(asMultiline(5, 5, "ok", "ok", false, "ok"), output)
    }

    @Test
    fun test_comparison() {
        val output = TestingHelper.loadAndRun("expression/comparison")
        assertEquals(asMultiline(true, false, false, true, true, false, false, false, false, true, true, true, false, false, true, true), output)
    }

    @Test
    fun test_inversion() {
        val output = TestingHelper.loadAndRun("expression/inversion")
        assertEquals(
            asMultiline(
                true, false,
                true, true, false, true, true, false, true, true,
                false, true, false, false, false, false, true, false
            ),
            output
        )
    }
}