package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.Set;

/**
 * Represents class value
 */
@NullMarked
public interface ClassValue extends ModifierableValue {
    /**
     * @return Id of this class's environment
     */
    String getId();

    //TODO
    boolean isInterface();

    /**
     * @return Base classes
     */
    Set<String> getBaseClasses();

    /**
     * @return Environment
     */
    ClassEnvironment getEnvironment();

    //TODO
    default ClassDesc asClassDesc() {
        return ClassDesc.of(EnvironmentUtils.getPackageName(getEnvironment()).orElseThrow() + "." + getId());
    }
}
