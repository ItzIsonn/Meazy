package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
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

    @Nullable
    default FileEnvironment getFileEnvironment(String packageName) {
        for (FileEnvironment fileEnvironment : getFileEnvironments()) {
            if (fileEnvironment.getPackageName().equals(packageName)) return fileEnvironment;
        }

        return null;
    }

    /**
     * @return All file environments
     */
    Set<FileEnvironment> getFileEnvironments();

    @Override
    @Nullable
    default ClassDeclarationEnvironment getClassDeclarationEnvironment(ClassDesc classDesc) {
//        System.out.println("Inside Global Looking for classDesc " + classDesc);

        FileEnvironment fileEnvironment = getFileEnvironment(classDesc.packageName());
        if (fileEnvironment == null) return null;

        ClassValue classValue = fileEnvironment.getLocalClass(classDesc.packageName());
        if (classValue != null) return classValue.getEnvironment().getParent();
        return null;
    }
}