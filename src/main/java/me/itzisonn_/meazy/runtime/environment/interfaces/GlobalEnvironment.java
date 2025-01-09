package me.itzisonn_.meazy.runtime.environment.interfaces;

import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ClassDeclarationEnvironment;

/**
 * GlobalEnvironment represents main environment
 */
public interface GlobalEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {}