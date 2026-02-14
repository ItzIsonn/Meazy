package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.Set;

/**
 * Represents class value
 */
@NullMarked
public interface ClassValue extends ModifierableRuntimeValue {
    /**
     * @return Id of this class's environment
     */
    String getId();

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
        return ClassDesc.of(getEnvironment().getPackageName() + "." + getId());
    }


    /**
     * @param value Value to check
     * @return Whether given value matches this class value
     */
    boolean isMatches(Object value);

    /**
     * @param fileEnvironment File environment
     * @param value Value to check
     * @return Whether given value matches this class value or it's base classes
     */
    boolean isLikeMatches(FileEnvironment fileEnvironment, Object value);
}
