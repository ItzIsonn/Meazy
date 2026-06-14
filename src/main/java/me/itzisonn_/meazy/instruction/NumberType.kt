package me.itzisonn_.meazy.instruction

import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

enum class NumberType(val classDesc: ClassDesc, val isBoxed: Boolean) {
    INT(ConstantDescs.CD_int, false),
    LONG(ConstantDescs.CD_long, false),
    FLOAT(ConstantDescs.CD_float, false),
    DOUBLE(ConstantDescs.CD_double, false),

    BOXED_INT(ConstantDescs.CD_Integer, true),
    BOXED_LONG(ConstantDescs.CD_Long, true),
    BOXED_FLOAT(ConstantDescs.CD_Float, true),
    BOXED_DOUBLE(ConstantDescs.CD_Double, true);

    val isInt get() = this == INT || this == BOXED_INT
    val isLong get() = this == LONG || this == BOXED_LONG
    val isFloat get() = this == FLOAT || this == BOXED_FLOAT
    val isDouble get() = this == DOUBLE || this == BOXED_DOUBLE

    fun box(): NumberType {
        return when (this) {
            INT -> BOXED_INT
            LONG -> BOXED_LONG
            FLOAT -> BOXED_FLOAT
            DOUBLE -> BOXED_DOUBLE
            else -> this
        }
    }

    fun unbox(): NumberType {
        return when (this) {
            BOXED_INT -> INT
            BOXED_LONG -> LONG
            BOXED_FLOAT -> FLOAT
            BOXED_DOUBLE -> DOUBLE
            else -> this
        }
    }


    companion object {
        fun valueOf(classDesc: ClassDesc): NumberType? {
            for (numberType in entries) {
                if (numberType.classDesc == classDesc) return numberType
            }

            return null
        }

        fun isNumberType(classDesc: ClassDesc): Boolean {
            return valueOf(classDesc) != null
        }

        fun getCommonUnboxed(a: NumberType, b: NumberType): NumberType {
            if (a.isDouble || b.isDouble) return DOUBLE
            if (a.isFloat && b.isLong) return DOUBLE
            if (a.isLong && b.isFloat) return DOUBLE

            if (a.isFloat || b.isFloat) return FLOAT
            if (a.isInt && b.isInt) return INT
            return LONG
        }
    }
}
