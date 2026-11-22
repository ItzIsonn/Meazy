package me.itzisonn_.meazy.instruction

import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

fun InstructionsSet.invertBoolean() {
    val trueLabel = createAndInitLabel()
    val endLabel = createAndInitLabel()
    gotoLabelIfEqualsZero(trueLabel)

    loadConstant(0)
    gotoLabel(endLabel)

    bindLabel(trueLabel)
    loadConstant(1)

    bindLabel(endLabel)
}



fun InstructionsSet.boxPrimitive(classDesc: ClassDesc) {
    val boxedClassDesc = classDesc.boxed
    if (boxedClassDesc == ConstantDescs.CD_Void) return

    invokeMethod(
        boxedClassDesc,
        "valueOf",
        MethodTypeDesc.of(boxedClassDesc, classDesc),
        InvokeType.STATIC
    )
}

fun InstructionsSet.unboxPrimitive(classDesc: ClassDesc) {
    val boxedClassDesc = classDesc.boxed
    if (boxedClassDesc == ConstantDescs.CD_Void) return

    invokeMethod(
        boxedClassDesc,
        classDesc.displayName() + "Value",
        MethodTypeDesc.of(classDesc),
        InvokeType.VIRTUAL
    )
}

fun InstructionsSet.convertPrimitiveOrBoxed(from: ClassDesc, to: ClassDesc, environment: Environment): Boolean {
    if (from == to) return true

    if (from.isPrimitive && !to.isPrimitive) {
        boxPrimitive(from)
        return true
    }

    if (environment.isInstanceOf(from, to)) return true

    val fromNumberType = NumberType.valueOf(from)
    val toNumberType = NumberType.valueOf(to)

    if (fromNumberType != null && toNumberType != null) {
        convertToNumberType(fromNumberType, toNumberType)
        return true
    }
    else if (from.isBoolean && to.isBoolean) {
        convertToBooleanType(from.isClassOrInterface, to.isClassOrInterface)
        return true
    }

    return false
}



val ClassDesc.boxed: ClassDesc get() {
    if (!isPrimitive) return this

    return when (this) {
        ConstantDescs.CD_int -> ConstantDescs.CD_Integer
        ConstantDescs.CD_long -> ConstantDescs.CD_Long
        ConstantDescs.CD_float -> ConstantDescs.CD_Float
        ConstantDescs.CD_double -> ConstantDescs.CD_Double
        ConstantDescs.CD_boolean -> ConstantDescs.CD_Boolean
        ConstantDescs.CD_void -> ConstantDescs.CD_Void
        else -> this
    }
}

val ClassDesc.isBoolean: Boolean get() = this == ConstantDescs.CD_boolean || this == ConstantDescs.CD_Boolean