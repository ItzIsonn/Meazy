package me.itzisonn_.meazy.parser.ast.expression.collection_creation

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class MapCreationExpression(val map: Map<Expression, Expression>) : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        instructions.invokeConstructor(
            ClassDesc.of("java.util.HashMap"),
            MethodTypeDesc.of(ConstantDescs.CD_void, ClassDesc.of("java.util.Map"))
        ) {
            invokeMethod(
                ConstantDescs.CD_Map,
                "ofEntries",
                MethodTypeDesc.of(ConstantDescs.CD_Map, ClassDesc.of($$"java.util.Map$Entry").arrayType()),
                InvokeType.STATIC_INTERFACE
            ) {
                loadConstant(map.size)
                newReferenceArray(ClassDesc.of($$"java.util.Map$Entry"))

                map.entries.forEachIndexed { i, (key, value) ->
                    invokeMethod(
                        ClassDesc.of("java.util.Map"),
                        "entry",
                        MethodTypeDesc.of(
                            ClassDesc.of($$"java.util.Map$Entry"),
                            ConstantDescs.CD_Object,
                            ConstantDescs.CD_Object
                        ),
                        InvokeType.STATIC_INTERFACE
                    ) {
                        duplicate()
                        loadConstant(i)

                        key.emit(this, environment, this@MapCreationExpression)
                        value.emit(this, environment, this@MapCreationExpression)
                    }

                    storeReferenceIntoArray()
                }
            }
        }
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return DataType.ofNonNull(ClassDesc.of("java.util.HashMap"))
    }
}
