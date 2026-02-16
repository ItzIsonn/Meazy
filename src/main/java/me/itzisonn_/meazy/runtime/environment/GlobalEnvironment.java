package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.Optional;
import java.util.Set;

/**
 * Represents global environment
 */
@NullMarked
public interface GlobalEnvironment extends Environment {
    /**
     * Adds to this global environment file environment
     * @param fileEnvironment FileEnvironment to add
     */
    void addFileEnvironment(FileEnvironment fileEnvironment);

    default Optional<FileEnvironment> getFileEnvironment(String packageName) {
        for (FileEnvironment fileEnvironment : getFileEnvironments()) {
            if (packageName.equals(fileEnvironment.getPackageName())) return Optional.of(fileEnvironment);
        }

        return Optional.empty();
    }

    /**
     * @return All file environments
     */
    Set<FileEnvironment> getFileEnvironments();



    Optional<ClassValue> resolveJavaClass(ClassDesc classDesc);
}