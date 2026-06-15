package me.itzisonn_.meazy.instruction

import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.util.boxed
import me.itzisonn_.meazy.util.isBoolean
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