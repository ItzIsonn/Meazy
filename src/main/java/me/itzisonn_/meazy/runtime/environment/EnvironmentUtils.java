package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NullMarked
public final class EnvironmentUtils { //TODO CHECK javadoc for incomplete param info AND FULL TOO
    private EnvironmentUtils() {}



    /**
     * Searches for given environment as a parent in this environment and all parents
     * @param environment Environment to lookup
     * @return Whether this environment has requested parent
     */
    public static boolean hasParent(Environment environment, Environment target) {
        Environment parent = environment.getParent();
        return target.equals(parent) || (parent != null && hasParent(parent, target));
    }

    /**
     * Searches for environment that matches given predicate in all parents of given environment
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     */
    public static boolean hasParent(Environment environment, Predicate<@Nullable Environment> predicate) {
        Environment parent = environment.getParent();
        return predicate.test(parent) || parent != null && hasParent(parent, predicate);
    }

    /**
     * Searches for environment that matches given predicate in given environment and all of its parents
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     */
    public static boolean hasParentOrSelf(Environment environment, Predicate<@Nullable Environment> predicate) {
        if (predicate.test(environment)) return true;
        return hasParent(environment, predicate);
    }

    /**
     * Searches for environment that is instance of given class in all parents of given environment
     * @param cls Class of parent environment
     * @return Whether this environment has parent of given class
     */
    public static <T extends Environment> boolean hasParent(Environment environment, Class<T> cls) {
        Environment parent = environment.getParent();
        if (cls.isInstance(parent)) return true;
        if (parent != null) return hasParent(parent, cls);
        return false;
    }

    /**
     * Searches for environment that is instance of given class in given environment and all of its parents
     * @param cls Class of parent environment
     * @return Whether this environment or its parent is instance of given class
     */
    public static <T extends Environment> boolean hasParentOrSelf(Environment environment, Class<T> cls) {
        if (cls.isInstance(environment)) return true;
        return hasParent(environment, cls);
    }



    /**
     * Searches for environment that matches given predicate in all parents of given environment
     * @param predicate Predicate that matches parent environment
     * @return Parent that matches given predicate or null
     */
    public static Optional<Environment> getParent(Environment environment, Predicate<@Nullable Environment> predicate) {
        Environment parent = environment.getParent();
        if (predicate.test(parent)) return Optional.ofNullable(parent);
        if (parent != null) return getParent(parent, predicate);
        return Optional.empty();
    }

    /**
     * Searches for environment that matches given predicate in given environment and all of its parents
     * @param predicate Predicate that matches parent environment
     * @return Parent or given environment that matches given predicate or null
     */
    public static Optional<Environment> getParentOrSelf(Environment environment, Predicate<@Nullable Environment> predicate) {
        if (predicate.test(environment)) return Optional.of(environment);
        return getParent(environment, predicate);
    }

    /**
     * Searches for environment that is instance of given class in all parents of given environment
     * @param cls Class of parent environment
     * @return Parent of given class or null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Environment> Optional<T> getParent(Environment environment, Class<T> cls) {
        Environment parent = environment.getParent();
        if (cls.isInstance(parent)) return Optional.of((T) parent);

        if (parent != null) return getParent(parent, cls);
        return Optional.empty();
    }

    /**
     * Searches for environment that is instance of given class in given environment and all of its parents
     * @param cls Class of parent environment
     * @return Parent or given environment of given class or null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Environment> Optional<T> getParentOrSelf(Environment environment, Class<T> cls) {
        if (cls.isInstance(environment)) return Optional.of((T) environment);
        return getParent(environment, cls);
    }



    /**
     * TODO
     */
    public static boolean isInstanceOf(Environment environment, ClassDesc classDesc, ClassDesc target) {
        if (classDesc.equals(target)) return true;

        ClassEnvironment classEnvironment = getClassEnvironment(environment, classDesc).orElse(null);
        if (classEnvironment == null) return false;

        if (classEnvironment.getBaseClass() != null && isInstanceOf(environment, classEnvironment.getBaseClass(), target)) return true;

        for (ClassDesc interfaceClassDesc : classEnvironment.getInterfaces()) {
            if (isInstanceOf(environment, interfaceClassDesc, target)) return true;
        }

        return false;
    }

    /**
     * TODO
     */
    @Nullable
    public static ClassDesc getCommonOf(Environment environment, ClassDesc classDesc1, ClassDesc classDesc2) {
        if (classDesc1.equals(classDesc2)) return classDesc1;
        if (isInstanceOf(environment, classDesc1, classDesc2)) return classDesc2;
        if (isInstanceOf(environment, classDesc2, classDesc1)) return classDesc1;

        ClassEnvironment classEnvironment = getClassEnvironment(environment, classDesc1).orElse(null);
        if (classEnvironment == null) return null;

        boolean gotObjectAsCommon = false;

        if (classEnvironment.getBaseClass() != null) {
            ClassDesc commonClassDesc = getCommonOf(environment, classEnvironment.getBaseClass(), classDesc2);
            if (ConstantDescs.CD_Object.equals(commonClassDesc)) gotObjectAsCommon = true;
            else if (commonClassDesc != null) return commonClassDesc;
        }

        for (ClassDesc interfaceClassDesc : classEnvironment.getInterfaces()) {
            ClassDesc commonClassDesc = getCommonOf(environment, interfaceClassDesc, classDesc2);
            if (commonClassDesc != null) return commonClassDesc;
        }

        if (gotObjectAsCommon) return ConstantDescs.CD_Object;
        return null;
    }

    /**
     * TODO
     */
    public static ClassDesc resolveClassDesc(Environment environment, ClassDesc classDesc, boolean allowPrimitives) {
        if (classDesc.isPrimitive() || classDesc.isArray()) return classDesc;
        if (!classDesc.packageName().isEmpty()) return classDesc;

        FileEnvironment fileEnvironment = getParentOrSelf(environment, FileEnvironment.class).orElse(null);
        if (fileEnvironment == null) return classDesc;

        ClassEnvironment classEnvironment = fileEnvironment.getClass(classDesc.displayName()).orElse(null);
        if (classEnvironment != null) return classEnvironment.getClassDesc();

        classEnvironment = getClassEnvironment(environment, classDesc).orElse(null);
        if (classEnvironment != null) return classEnvironment.getClassDesc();

        String fullId;
        ClassDesc fullClassDesc = fileEnvironment.getImports().get(classDesc.displayName());
        if (fullClassDesc != null) {
            if (fullClassDesc.isPrimitive() || classDesc.isArray()) return fullClassDesc;
            fullId = (fullClassDesc.packageName().isEmpty() ? "" : fullClassDesc.packageName() + ".") + fullClassDesc.displayName();
        }
        else fullId = classDesc.displayName();

        if (allowPrimitives) {
            if (classDesc.displayName().equals("Int")) return ConstantDescs.CD_int;
            if (classDesc.displayName().equals("Long")) return ConstantDescs.CD_long;
            if (classDesc.displayName().equals("Float")) return ConstantDescs.CD_float;
            if (classDesc.displayName().equals("Double")) return ConstantDescs.CD_double;
            if (classDesc.displayName().equals("Boolean")) return ConstantDescs.CD_boolean;
        }

        classEnvironment = getClassEnvironment(environment, fullId).orElse(null);
        if (classEnvironment != null) return classEnvironment.getClassDesc();

        ClassDesc resolvedClassDesc = ClassDesc.of(fullId);
        fileEnvironment.getParent().resolveJavaClass(resolvedClassDesc);
        return resolvedClassDesc;
    }

    /**
     * TODO
     */
    public static ClassDesc resolveClassDesc(Environment environment, String classDesc, boolean allowPrimitives) {
        return resolveClassDesc(environment, ClassDesc.of(classDesc), allowPrimitives);
    }



    /**
     * @return File environment in parent environments of given environment
     */
    public static Optional<FileEnvironment> getFileEnvironment(Environment environment) {
        if (environment instanceof FileEnvironment fileEnvironment) return Optional.of(fileEnvironment);
        return getParent(environment, FileEnvironment.class);
    }

    public static Optional<String> getClassName(Environment environment) {
        FileEnvironment parent = getFileEnvironment(environment).orElse(null);
        if (parent == null) return Optional.empty();
        return Optional.of(parent.getClassName());
    }

    /**
     * @return Package name of this environment
     */
    public static Optional<String> getPackageName(Environment environment) {
        FileEnvironment parent = getFileEnvironment(environment).orElse(null);
        if (parent == null) return Optional.empty();
        return Optional.of(parent.getPackageName());
    }

    /**
     * @return Are two given environments from same package
     */
    public static boolean areFromSamePackage(Environment environment1, Environment environment2) {
        return getPackageName(environment1).equals(getPackageName(environment2));
    }



    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return Environment that has requested variable or null
     */
    public static Optional<VariableDeclarationEnvironment> getVariableDeclarationEnvironment(Environment environment, String id) {
        return getVariableValue(environment, id).map(VariableValue::getParentEnvironment);
    }

    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return Environment that has requested variable or null
     */
    public static Optional<VariableValue> getVariableValue(Environment environment, String id) {
        if (environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment) {
            Optional<VariableValue> variableValue = variableDeclarationEnvironment.getVariable(id);
            if (variableValue.isPresent()) return variableValue;
        }

        if (environment instanceof GlobalEnvironment globalEnvironment) {
            for (FileEnvironment fileEnvironment : globalEnvironment.getFileEnvironments()) {
                Optional<VariableValue> variableValue = fileEnvironment.getVariable(id);
                if (variableValue.isPresent()) return variableValue;
            }

            return Optional.empty();
        }

        Environment parent = environment.getParent();
        if (parent == null) return Optional.empty();
        return getVariableValue(parent, id);
    }



    /**
     * Searches for function with given id and args in this environment and all parents
     *
     * @param id Function's id
     * @param parameters Function's parameters
     * @return Environment that has requested function or null
     */
    public static Optional<FunctionDeclarationEnvironment> getFunctionDeclarationEnvironment(Environment environment, String id, List<ClassDesc> parameters) {
        return getFunctionValue(environment, id, parameters).map(FunctionValue::getEnvironment).map(FunctionEnvironment::getParent);
    }

    /**
     * Searches for function with given id and args in this environment and all parents
     *
     * @param id Function's id
     * @param parameters Function's parameters
     * @return Environment that has requested function or null
     */
    public static Optional<FunctionValue> getFunctionValue(Environment environment, String id, List<ClassDesc> parameters) {
        if (environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment) {
            Optional<FunctionValue> functionValue = functionDeclarationEnvironment.getFunction(id, parameters);
            if (functionValue.isPresent()) return functionValue;
        }

        if (environment instanceof GlobalEnvironment globalEnvironment) {
            for (FileEnvironment fileEnvironment : globalEnvironment.getFileEnvironments()) {
                Optional<FunctionValue> functionValue = fileEnvironment.getFunction(id, parameters);
                if (functionValue.isPresent()) return functionValue;
            }

            return Optional.empty();
        }

        Environment parent = environment.getParent();
        if (parent == null) return Optional.empty();
        return getFunctionValue(parent, id, parameters);
    }



    public static Optional<ClassDeclarationEnvironment> getClassDeclarationEnvironment(Environment environment, String id) {
        return getClassDeclarationEnvironment(environment, ClassDesc.of(id));
    }

    public static Optional<ClassDeclarationEnvironment> getClassDeclarationEnvironment(Environment environment, ClassDesc classDesc) {
        return getClassEnvironment(environment, classDesc).map(ClassEnvironment::getParent);
    }

    public static Optional<ClassEnvironment> getClassEnvironment(Environment environment, String id) {
        return getClassEnvironment(environment, ClassDesc.of(id));
    }

    public static Optional<ClassEnvironment> getClassEnvironment(Environment environment, ClassDesc classDesc) {
        if (classDesc.isPrimitive() || classDesc.isArray()) return Optional.empty();

        GlobalEnvironment globalEnvironment = getParentOrSelf(environment, GlobalEnvironment.class).orElse(null);
        if (globalEnvironment == null) return Optional.empty();

        for (FileEnvironment fileEnvironment : globalEnvironment.getFileEnvironments(classDesc.packageName())) {
            Optional<ClassEnvironment> classEnvironment = fileEnvironment.getClass(classDesc.displayName());
            if (classEnvironment.isPresent()) return classEnvironment;
        }

        return globalEnvironment.resolveJavaClass(classDesc);
    }
}
