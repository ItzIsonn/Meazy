package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.ClassEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.Set;

@NullMarked
public class ClassEnvironmentFactoryImpl implements ClassEnvironmentFactory {
    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, @Nullable ClassDesc baseClass, Set<ClassDesc> interfaces, Set<Modifier> modifiers) {
        return new ClassEnvironmentImpl(parent, isShared, isInterface, id, baseClass, interfaces, modifiers);
    }

    @Override
    public ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, Set<String> unresolvedBaseClasses, Set<Modifier> modifiers) {
        return new ClassEnvironmentImpl(parent, isShared, isInterface, id, unresolvedBaseClasses, modifiers);
    }
}
