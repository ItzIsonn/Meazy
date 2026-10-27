package me.itzisonn_.meazy

import me.itzisonn_.meazy.helper.TestingHelper
import org.junit.jupiter.api.BeforeAll
import kotlin.test.*

object MainFunctionTests {
    @BeforeAll
    @JvmStatic
    fun setup() {
        TestingHelper.initialize()
    }

    @Test
    fun test_simple() {
        val output = TestingHelper.loadAndRun("main_function/simple")
        assertEquals("Hello world!\n", output)
    }

    @Test
    fun test_missing() {
        val output = TestingHelper.loadAndRun("main_function/missing")
        assertEquals("", output)
    }
}