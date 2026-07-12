package me.itzisonn_.meazy

import org.junit.jupiter.api.BeforeAll
import kotlin.test.*

object MainFunctionTest {
    @BeforeAll
    @JvmStatic
    fun setup() {
        MeazyMain.initialize()
    }

    @Test
    fun test1() {
        val output = TestingHelper.loadAndRun("main_function/1")
        assertEquals("Hello world!\n", output)
    }
}