package me.itzisonn_.meazy.util

import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

object MiscUtils {
    /**
     * Generates name with prefix:
     * - If given name is uppercase, returns value in format PREFIX_NAME
     * - Else returns value in format prefixName
     * 
     * @param prefix Prefix
     * @param name Name
     * @return Generated name
     */
    fun generatePrefixedName(prefix: String, name: String): String {
        if (name == name.uppercase()) return prefix.uppercase() + "_" + name
        return prefix + name.substring(0, 1).uppercase() + name.substring(1)
    }
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