package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.SymbolResolver.resolveVariable
import me.itzisonn_.meazy.parser.ast.parent
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import kotlin.uuid.Uuid

class Identifier(val id: String) : Expression {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val resolvedVariable = environment.resolveVariable(this)

        if (resolvedVariable.classDesc == null) {
            instructions.getLocal(resolvedVariable.type, resolvedVariable.slot)
        }
        else if (resolvedVariable.receiver == null) {
            instructions.getStaticField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
        }
        else {
            resolvedVariable.receiver.emit(instructions, environment)
            var endLabel: Uuid? = null

            val parent = parent
            if (parent is MemberExpression) {
                if (!parent.isNullSafe) {
                    if (resolvedVariable.receiver.getType(environment).isNullable) {
                        throw RuntimeException("Unsafe member access of variable '$id' on object of type ${resolvedVariable.classDesc.descriptorString()}")
                    }
                }
                else {
                    val nonnullLabel = instructions.createAndInitLabel()
                    endLabel = instructions.createAndInitLabel()

                    instructions.duplicate()
                    instructions.gotoLabelIfNonNull(nonnullLabel)

                    instructions.pop()
                    instructions.loadNull()
                    instructions.gotoLabel(endLabel)

                    instructions.bindLabel(nonnullLabel)
                }
            }

            instructions.getField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)

            if (endLabel != null) {
                instructions.bindLabel(endLabel)
            }
        }
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val parent = parent

        if (parent is MemberExpression && this != parent.member) {
            val classDesc = environment.resolveClassDesc(id, false)

            if (environment.getClass(classDesc) != null) {
                return DataType.ofNonNull(classDesc)
            }
        }

        val resolvedVariable = environment.resolveVariable(this)
        return DataType.of(resolvedVariable.type, resolvedVariable.isNullable)
    }
}