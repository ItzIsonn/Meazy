package me.itzisonn_.meazy.parser.ast.expression.collection_creation

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ListCreationExpression(val list: List<Expression>) : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        instructions.invokeConstructor(
            ClassDesc.of("java.util.ArrayList"),
            MethodTypeDesc.of(ConstantDescs.CD_void, ClassDesc.of("java.util.Collection"))
        ) { arrayListArgsInstructions(environment) }
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return DataType.ofNonNull(ClassDesc.of("java.util.ArrayList"))
    }

    private fun InstructionsSet.arrayListArgsInstructions(environment: Environment) {
        invokeMethod(
            ConstantDescs.CD_List,
            "of",
            MethodTypeDesc.of(ConstantDescs.CD_List, ConstantDescs.CD_Object.arrayType()),
            InvokeType.STATIC_INTERFACE
        ) { ofArgsInstructions(environment) }
    }

    private fun InstructionsSet.ofArgsInstructions(environment: Environment) {
        loadConstant(list.size)
        newReferenceArray(
            if (list.isEmpty()) ConstantDescs.CD_Object
            else list.first().getType(environment, this@ListCreationExpression).classDesc
        )

        list.forEachIndexed { i, arg ->
            duplicate()
            loadConstant(i)
            arg.emit(this, environment, this@ListCreationExpression)
            storeReferenceIntoArray()
        }
    }
}
