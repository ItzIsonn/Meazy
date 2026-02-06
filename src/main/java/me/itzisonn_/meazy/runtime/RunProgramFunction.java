package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.ast.program.Program;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.Map;

/**
 * Represents function that is used to run {@link Program}s
 * @see Registries#RUN_PROGRAM_FUNCTION
 */
@FunctionalInterface
@NullMarked
public interface RunProgramFunction {
    /**
     * Runs given program
     * @param classes Program to run TODO
     */
    void run(Map<ClassDesc, byte[]> classes);
}
