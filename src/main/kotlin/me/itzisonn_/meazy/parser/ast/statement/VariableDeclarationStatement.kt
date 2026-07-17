package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import java.lang.reflect.AccessFlag

class VariableDeclarationStatement(
    modifiers: Set<Modifier>,
    val isConstant: Boolean,
    val id: String,
    val dataType: DataType?,
    val value: Expression?
) : ModifierStatement(modifiers), DeclarationStatement, LocalStatement {
    private lateinit var symbol: VariableSymbol
    private var declared = false

    override val children = let {
        if (value != null) setOf(value)
        else setOf()
    }

    context(parents: ParentMap)
    override fun declare(environment: Environment) {
        if (environment !is VariableDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE variable HERE TODO")
        }
        val dataType = dataType ?: value?.getType(environment)
            ?: error("Variable without a data type must have initializer TODO")

        symbol = environment.declareVariable(id, dataType, isConstant, value, modifiers)
        declared = true
    }

    context(parents: ParentMap)
    override fun resolve(environment: Environment) {
        symbol.dataType.resolve(environment)
    }

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        if (!declared) {
            if (environment !is FileEnvironment && environment !is ClassEnvironment) {
                declare(environment)
                resolve(environment)
            }
            else throw RuntimeException("Declared variable is unresolved TODO")
        }

        val variableType = symbol.dataType.classDesc

        if (environment is FileEnvironment) {
            val accessFlags = mutableSetOf(AccessFlag.STATIC)
            if (isConstant) accessFlags.add(AccessFlag.FINAL)

            if (Modifiers.private in modifiers) accessFlags.add(AccessFlag.PRIVATE)
            else accessFlags.add(AccessFlag.PUBLIC)

            instructions.withField(id, variableType, accessFlags)
            return
        }

        if (environment is ClassEnvironment) {
            val accessFlags = mutableSetOf<AccessFlag>()
            if (Modifiers.private in modifiers) accessFlags.add(AccessFlag.PRIVATE)
            else if (Modifiers.protected in modifiers) accessFlags.add(AccessFlag.PROTECTED)
            else accessFlags.add(AccessFlag.PUBLIC)

            if (Modifiers.shared in modifiers) accessFlags.add(AccessFlag.STATIC)
            if (isConstant) accessFlags.add(AccessFlag.FINAL)

            instructions.withField(id, variableType, accessFlags)
            return
        }


        if (value != null) {
            value.emit(instructions, environment)
            val valueType = value.getType(environment).classDesc

            if (!environment.isInstanceOf(valueType, variableType)) {
                if (!instructions.convertPrimitiveOrBoxed(valueType, variableType)) {
                    throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
                }
            }
        }

        instructions.storeLocal(variableType, symbol.slot)

        if (environment is LocalVariableDeclarationEnvironment) {
            if (environment.getStartLabel() == null || environment.getEndLabel() == null) return

            instructions.setLocalName(
                symbol.slot, id, variableType,
                environment.getStartLabel()!!, environment.getEndLabel()!!
            )
        }
    }

    override fun alwaysReturns() = false
}
