package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import me.itzisonn_.meazy.util.MiscUtils.boxPrimitive
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class IsExpression(
    val value: Expression,
    val dataType: String,
    val isLike: Boolean
) : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val classDesc = environment.resolveClassDesc(dataType, false)
        val valueClassDesc = value.getType(environment, this).classDesc

        value.emit(instructions, environment, this)
        if (valueClassDesc.isPrimitive) boxPrimitive(instructions, valueClassDesc)

        if (isLike) {
            instructions.instanceOf(classDesc)
            return
        }

        instructions.invokeMethod(
            ConstantDescs.CD_Object,
            "getClass",
            MethodTypeDesc.of(ConstantDescs.CD_Class),
            InvokeType.VIRTUAL
        )

        instructions.invokeMethod(
            ConstantDescs.CD_Object,
            "equals",
            MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object),
            InvokeType.VIRTUAL
        ) { loadConstant(classDesc) }
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}