package me.itzisonn_.meazy.context;

import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;

/**
 * Represents runtime context
 */
@Getter
public class RuntimeContext {
    private final Interpreter interpreter;
    private final GlobalEnvironment globalEnvironment;

    public RuntimeContext() {
        interpreter = new Interpreter(this);
        globalEnvironment = Registries.GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue().create(this);

        for (FileEnvironment fileEnvironment : Registries.NATIVE_FILE_ENVIRONMENTS) {
            globalEnvironment.addFileEnvironment(fileEnvironment);
        }
    }
}
