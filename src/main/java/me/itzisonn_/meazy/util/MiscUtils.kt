package me.itzisonn_.meazy.util

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.util.*

object MiscUtils {
    /**
     * Generates name with prefix:<br></br>
     * - If given name is uppercase, returns value in format PREFIX_NAME<br></br>
     * - Else returns value in format prefixName
     * 
     * @param prefix Prefix
     * @param name Name
     * @return Generated name
     */
    fun generatePrefixedName(prefix: String, name: String): String {
        if (name == name.uppercase(Locale.getDefault())) return prefix.uppercase(Locale.getDefault()) + "_" + name
        return prefix + name.substring(0, 1).uppercase(Locale.getDefault()) + name.substring(1)
    }

    fun isBoolean(classDesc: ClassDesc): Boolean {
        return classDesc == ConstantDescs.CD_boolean || classDesc == ConstantDescs.CD_Boolean
    }

    fun getBoxedType(classDesc: ClassDesc): ClassDesc {
        if (!classDesc.isPrimitive) return classDesc

        return when (classDesc.descriptorString()) {
            "I" -> ConstantDescs.CD_Integer
            "J" -> ConstantDescs.CD_Long
            "F" -> ConstantDescs.CD_Float
            "D" -> ConstantDescs.CD_Double
            "Z" -> ConstantDescs.CD_Boolean
            else -> classDesc
        }
    }

    fun boxPrimitive(instructionsSet: InstructionsSet, classDesc: ClassDesc) {
        val boxedClassDesc = getBoxedType(classDesc)
        if (boxedClassDesc == ConstantDescs.CD_void) return

        instructionsSet.invokeMethod(
            boxedClassDesc,
            "valueOf",
            MethodTypeDesc.of(boxedClassDesc, classDesc),
            InvokeType.STATIC
        )
    }

    fun convertPrimitiveOrBoxed(instructionsSet: InstructionsSet, from: ClassDesc, to: ClassDesc): Boolean {
        val fromNumberType = NumberType.valueOf(from)
        val toNumberType = NumberType.valueOf(to)

        if (fromNumberType != null && toNumberType != null) {
            instructionsSet.convertToNumberType(fromNumberType, toNumberType)
            return true
        }
        else if (isBoolean(from) && isBoolean(to)) {
            instructionsSet.convertToBooleanType(from.isClassOrInterface, to.isClassOrInterface)
            return true
        }

        return false
    }
}