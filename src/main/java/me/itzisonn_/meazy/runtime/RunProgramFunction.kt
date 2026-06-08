package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.parser.ast.program.Program
import java.lang.constant.ClassDesc

/**
 * Represents function that is used to run [Program]s
 * @see Registries.RUN_PROGRAM_FUNCTION
 */
fun interface RunProgramFunction {
    /**
     * Runs given program
     * @param classes Program to run TODO
     */
    fun run(classes: Map<ClassDesc, ByteArray>)
}
