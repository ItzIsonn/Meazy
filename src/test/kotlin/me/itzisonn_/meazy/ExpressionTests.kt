@file:Suppress("KotlinMisorderedAssertEqualsArguments")

package me.itzisonn_.meazy

import me.itzisonn_.meazy.helper.TestingHelper
import me.itzisonn_.meazy.helper.asMultiline
import org.junit.jupiter.api.BeforeAll
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
    fun test_order_of_operations() {
        val output = TestingHelper.loadAndRun("expression/order_of_operations")
        assertEquals(asMultiline(7, 7, 9, 8, 10, 10, 8, 2, 54f), output)
    }
}