package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;

/**
 * Represents function that is used to evaluate {@link Program}s
 * @see Registries#EVALUATE_PROGRAM_FUNCTION
 */
public interface EvaluateProgramFunction {
    /**
     * Evaluates given program using given globalEnvironment
     *
     * @param program Program to evaluate
     * @param globalEnvironment Global environment
     *
     * @return File environment of given program
     */
    FileEnvironment evaluate(Program program, GlobalEnvironment globalEnvironment);
}
