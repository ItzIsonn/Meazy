package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ForeachStatement(
    val isConstant: Boolean,
    val id: String,
    val dataType: DataType,
    val collection: Expression,
    val body: List<LocalStatement>
) : LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        if (environment !is LocalVariableDeclarationEnvironment) {
            throw RuntimeException("Foreach statement must be inside variableDeclarationEnvironment TODO")
        }

        val iterableVariableValue = environment.declareVariable(
            DataType.ofNonNull(ClassDesc.of("java.lang.Iterable")),
            true,
            null
        )

        collection.emit(instructions, environment, this)

        instructions.invokeMethod(
            ClassDesc.of("java.lang.Iterable"),
            "iterator",
            MethodTypeDesc.of(ClassDesc.of("java.util.Iterator")),
            InvokeType.INTERFACE
        )

        instructions.storeLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.slot)

        val conditionLabel = instructions.createAndInitLabel()
        val endLabel = instructions.createAndInitLabel()
        val loopEnvironment = LoopEnvironment(environment, conditionLabel, endLabel)

        instructions.bindLabel(conditionLabel)

        instructions.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.slot)
        instructions.invokeMethod(
            ClassDesc.of("java.util.Iterator"),
            "hasNext",
            MethodTypeDesc.of(ConstantDescs.CD_boolean),
            InvokeType.INTERFACE
        )
        instructions.gotoLabelIfEqualsZero(endLabel)

        instructions.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.slot)
        instructions.invokeMethod(
            ClassDesc.of("java.util.Iterator"),
            "next",
            MethodTypeDesc.of(ConstantDescs.CD_Object),
            InvokeType.INTERFACE
        )

        dataType.resolve(environment)
        instructions.checkCast(dataType.classDesc)

        val variableValue = environment.declareVariable(id, dataType, isConstant, null)
        instructions.storeLocal(variableValue.dataType.classDesc, variableValue.slot)
        instructions.setLocalName(
            variableValue.slot,
            id,
            variableValue.dataType.classDesc,
            conditionLabel,
            endLabel
        )

        for (statement in body) {
            statement.emit(instructions, loopEnvironment, this)
        }

        instructions.gotoLabel(conditionLabel)
        instructions.bindLabel(endLabel)
    }

    override fun alwaysReturns(): Boolean {
        for (localStatement in body) {
            if (localStatement.alwaysReturns()) return true
        }

        return false
    }
}
