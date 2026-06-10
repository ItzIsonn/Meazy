package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression
import me.itzisonn_.meazy.parser.modifier.Modifier
import kotlin.uuid.Uuid

/**
 * Represents environment for constructors
 */
interface ConstructorEnvironment : LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    override fun getParent(): ConstructorDeclarationEnvironment

    val parameters: List<ParameterExpression>
}



class ConstructorEnvironmentImpl(
    parent: ConstructorDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    modifiers: MutableSet<Modifier>,
    parameters: MutableList<ParameterExpression>
) : LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel), ConstructorEnvironment {
    override val isShared get() = false
    override fun getParent() = super.getParent() as ConstructorDeclarationEnvironment

    override val modifiers = modifiers.toSet()
        get() = field.toSet()

    override val parameters = parameters.toList()
        get() = field.toList()
}