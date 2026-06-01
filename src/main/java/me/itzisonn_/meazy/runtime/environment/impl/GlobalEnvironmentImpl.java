package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@NullMarked
public class GlobalEnvironmentImpl implements GlobalEnvironment {
    private final Set<FileEnvironment> fileEnvironments;

    public GlobalEnvironmentImpl() {
        fileEnvironments = new HashSet<>();
    }

    @Override
    public void addFileEnvironment(FileEnvironment fileEnvironment) {
        fileEnvironments.add(fileEnvironment);
    }

    @Override
    public Set<FileEnvironment> getFileEnvironments() {
        return new HashSet<>(fileEnvironments);
    }

    @Override
    @Nullable
    public Environment getParent() {
        return null;
    }

    @Override
    public boolean isShared() {
        return false;
    }



    @Override
    public Optional<ClassValue> resolveJavaClass(ClassDesc classDesc) {
        if (classDesc.isPrimitive() || classDesc.isArray()) return Optional.empty();

        try {
            Class<?> cls = Class.forName(classDesc.packageName() + "." + classDesc.displayName());
            String packageName = cls.getPackageName();

            for (FileEnvironment fileEnvironment : getFileEnvironments(packageName)) {
                Optional<ClassValue> classValue = fileEnvironment.getClass(classDesc.displayName());
                if (classValue.isPresent()) return classValue;
            }

            FileEnvironment fileEnvironment = Registries.FILE_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                    this, packageName, cls.getSimpleName()
            );

            Set<me.itzisonn_.meazy.parser.modifier.Modifier> classEnvironmentModifiers = new HashSet<>();
            if (!Modifier.isFinal(cls.getModifiers())) classEnvironmentModifiers.add(Modifiers.OPEN());
            if (Modifier.isPrivate(cls.getModifiers())) classEnvironmentModifiers.add(Modifiers.PRIVATE());
            if (Modifier.isAbstract(cls.getModifiers())) classEnvironmentModifiers.add(Modifiers.ABSTRACT());

            ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                    fileEnvironment,
                    false,
                    cls.isInterface(),
                    classDesc.displayName(),
                    cls.getSuperclass() == null ? null : cls.getSuperclass().describeConstable().orElseThrow(),
                    Arrays.stream(cls.getInterfaces()).map(c -> c.describeConstable().orElseThrow()).collect(Collectors.toSet()),
                    classEnvironmentModifiers
            );

            ClassValue classValue = fileEnvironment.declareClass(classEnvironment);
            addFileEnvironment(fileEnvironment);

            if (classEnvironment.getBaseClass() != null) resolveJavaClass(classEnvironment.getBaseClass());
            for (ClassDesc interfaceClassDesc : classEnvironment.getInterfaces()) {
                resolveJavaClass(interfaceClassDesc);
            }

            for (Method method : cls.getDeclaredMethods()) {
                if (method.isSynthetic()) continue;

                DataType returnDataType;
                if (method.getReturnType() == void.class) returnDataType = null;
                else {
                    ClassDesc returnTypeDesc = method.getReturnType().describeConstable().orElseThrow();
                    resolveJavaClass(returnTypeDesc);
                    returnDataType = DataType.of(returnTypeDesc, !method.getReturnType().isPrimitive());
                }

                Set<me.itzisonn_.meazy.parser.modifier.Modifier> functionModifiers = new HashSet<>();
                if (!Modifier.isFinal(cls.getModifiers())) functionModifiers.add(Modifiers.OPEN());
                if (Modifier.isPrivate(cls.getModifiers())) functionModifiers.add(Modifiers.PRIVATE());
                if (Modifier.isProtected(cls.getModifiers())) functionModifiers.add(Modifiers.PROTECTED());
                if (Modifier.isStatic(cls.getModifiers())) functionModifiers.add(Modifiers.SHARED());
                if (Modifier.isAbstract(cls.getModifiers())) functionModifiers.add(Modifiers.ABSTRACT());

                classEnvironment.declareFunction(
                        method.getName(),
                        Arrays.stream(method.getParameters()).map(p -> new ParameterExpression(
                                p.getName(),
                                DataType.of(p.getType().describeConstable().orElseThrow(), !p.getType().isPrimitive()),
                                Modifier.isFinal(p.getModifiers())
                        )).toList(),
                        returnDataType,
                        Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                                classEnvironment,
                                null,
                                null,
                                returnDataType,
                                Modifier.isStatic(method.getModifiers()),
                                functionModifiers
                        )
                );
            }

            for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
                Set<me.itzisonn_.meazy.parser.modifier.Modifier> constructorModifiers = new HashSet<>();
                if (Modifier.isPrivate(cls.getModifiers())) constructorModifiers.add(Modifiers.PRIVATE());
                if (Modifier.isProtected(cls.getModifiers())) constructorModifiers.add(Modifiers.PROTECTED());

                classEnvironment.declareConstructor(
                        Arrays.stream(constructor.getParameters()).map(p -> new ParameterExpression(
                                p.getName(),
                                DataType.of(p.getType().describeConstable().orElseThrow(), !p.getType().isPrimitive()),
                                Modifier.isFinal(p.getModifiers())
                        )).toList(),

                        Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                                classEnvironment,
                                null,
                                null,
                                constructorModifiers
                        )
                );
            }

            for (Field field : cls.getDeclaredFields()) {
                boolean isNullable;
                if (field.getType().isPrimitive()) isNullable = false;
                else if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    if (field.trySetAccessible()) isNullable = field.get(null) == null;
                    else isNullable = true;
                }
                else isNullable = true;

                classEnvironment.declareVariable(
                        field.getName(),
                        DataType.of(field.getType().describeConstable().orElseThrow(), isNullable),
                        Modifier.isFinal(field.getModifiers()),
                        null
                );
            }

            return Optional.of(classValue);
        }
        catch (ClassNotFoundException e) {
            return Optional.empty();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}