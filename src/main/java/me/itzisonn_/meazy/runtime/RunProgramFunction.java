package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;

/**
 * Represents function that is used to run {@link Program}s
 * @see Registries#RUN_PROGRAM_FUNCTION
 */
public interface RunProgramFunction {
    /**
     * Runs given program
     * @param program Program to run
     * @return File environment of given program
     */
    FileEnvironment run(Program program);
}
