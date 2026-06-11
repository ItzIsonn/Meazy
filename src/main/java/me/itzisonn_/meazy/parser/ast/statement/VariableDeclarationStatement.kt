package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import me.itzisonn_.meazy.util.MiscUtils.convertPrimitiveOrBoxed
import java.lang.reflect.AccessFlag

class VariableDeclarationStatement(
    modifiers: Set<Modifier>,
    val isConstant: Boolean,
    val id: String,
    val dataType: DataType?,
    val value: Expression?
) : ModifierStatement(modifiers), DeclarationStatement, LocalStatement {
    private lateinit var variableValue: VariableValue
    private var declared = false

    override fun declare(environment: Environment) {
        if (environment !is VariableDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE variable HERE TODO")
        }
        val dataType = dataType ?: value?.getType(environment, this)
            ?: error("Variable without a data type must have initializer TODO")

        variableValue = environment.declareVariable(id, dataType, isConstant, value)
        declared = true
    }

    override fun resolve(environment: Environment) {
        variableValue.dataType.resolve(environment)
    }

    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        if (!declared) {
            if (environment !is FileEnvironment && environment !is ClassEnvironment) {
                declare(environment)
                resolve(environment)
            }
            else throw RuntimeException("Declared variable is unresolved TODO")
        }

        val variableType = variableValue.dataType.classDesc

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
            value.emit(instructions, environment, this)
            val valueType = value.getType(environment, this).classDesc

            if (!environment.isInstanceOf(valueType, variableType)) {
                if (!convertPrimitiveOrBoxed(instructions, valueType, variableType)) {
                    throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
                }
            }
        }

        instructions.storeLocal(variableType, variableValue.slot)

        if (environment is LocalVariableDeclarationEnvironment) {
            if (environment.getStartLabel() == null || environment.getEndLabel() == null) return

            instructions.setLocalName(
                variableValue.slot, id, variableType,
                environment.getStartLabel()!!, environment.getEndLabel()!!
            )
        }
    }

    override fun alwaysReturns() = false
}
