package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class IsExpression(
    val value: Expression,
    val dataType: String,
    val isLike: Boolean
) : Expression {
    override val children = setOf(value)

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val classDesc = environment.resolveClassDesc(dataType, false)
        val valueClassDesc = value.getType(environment).classDesc

        value.emit(instructions, environment)
        if (valueClassDesc.isPrimitive) instructions.boxPrimitive(valueClassDesc)

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

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}