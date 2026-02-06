package me.itzisonn_.meazy.parser;

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
public interface CompileProgramFunction { //TODO JAVADOC
    /**
     * Runs given program
     * @param program Program to run
     */
    Map<ClassDesc, byte[]> compile(Program program);
}
