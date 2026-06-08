package me.itzisonn_.meazy.runtime.environment.factory;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents factory for creating {@link LoopEnvironment}s
 */
@NullMarked
public interface LoopEnvironmentFactory {//TODO
    /**
     * Creates non-shared loop environment
     *
     * @param parent Parent
     * @return New loop environment
     */
    LoopEnvironment create(Environment parent, Uuid startLabel, Uuid endLabel);
}
