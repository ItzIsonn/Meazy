package me.itzisonn_.meazy.instruction

import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

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

fun InstructionsSet.convertPrimitiveOrBoxed(from: ClassDesc, to: ClassDesc): Boolean {
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

val ClassDesc.isBoolean: Boolean get() {
    return this == ConstantDescs.CD_boolean || this == ConstantDescs.CD_Boolean
}