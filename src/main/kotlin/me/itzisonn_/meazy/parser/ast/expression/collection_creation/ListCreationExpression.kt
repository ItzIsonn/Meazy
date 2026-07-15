package me.itzisonn_.meazy.parser.ast.expression.collection_creation

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ListCreationExpression(val list: List<Expression>) : Expression {
    override val children = list.toSet()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        instructions.invokeConstructor(
            ClassDesc.of("java.util.ArrayList"),
            MethodTypeDesc.of(ConstantDescs.CD_void, ClassDesc.of("java.util.Collection"))
        ) {
            invokeMethod(
                ConstantDescs.CD_List,
                "of",
                MethodTypeDesc.of(ConstantDescs.CD_List, ConstantDescs.CD_Object.arrayType()),
                InvokeType.STATIC_INTERFACE
            ) {
                loadConstant(list.size)
                newReferenceArray(
                    if (list.isEmpty()) ConstantDescs.CD_Object
                    else list.first().getType(environment).classDesc
                )

                list.forEachIndexed { i, arg ->
                    duplicate()
                    loadConstant(i)
                    arg.emit(this, environment)
                    storeReferenceIntoArray()
                }
            }
        }
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return DataType.ofNonNull(ClassDesc.of("java.util.ArrayList"))
    }
}
