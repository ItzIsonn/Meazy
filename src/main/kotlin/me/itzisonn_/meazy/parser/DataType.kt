package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getCommonOf
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents type of expressions
 */
@ConsistentCopyVisibility
data class DataType private constructor(
    var classDesc: ClassDesc,
    val isNullable: Boolean
) {
    override fun toString() = classDesc.displayName() + (if (isNullable) "?" else "")

    fun resolve(environment: Environment) {
        classDesc = environment.resolveClassDesc(classDesc, !isNullable)
    }

    fun with(classDesc: ClassDesc) = DataType(classDesc, isNullable)
    fun with(isNullable: Boolean) = DataType(classDesc, isNullable)
    fun asNullable() = DataType(classDesc, true)
    fun asNonNull() = DataType(classDesc, false)



    companion object {
        fun of(classDesc: ClassDesc, isNullable: Boolean) = DataType(classDesc, isNullable)
        fun ofNullable(classDesc: ClassDesc) = DataType(classDesc, true)
        fun ofNonNull(classDesc: ClassDesc) = DataType(classDesc, false)

        fun commonOf(environment: Environment, dataType1: DataType, dataType2: DataType): DataType {
            val classDesc = environment.getCommonOf(
                dataType1.classDesc,
                dataType2.classDesc
            )

            return of(
                classDesc ?: ConstantDescs.CD_Object,
                dataType1.isNullable || dataType2.isNullable
            )
        }

        fun matches(environment: Environment, dataType: DataType, target: DataType): Boolean {
            return environment.isInstanceOf(dataType.classDesc, target.classDesc)
                    && (!dataType.isNullable || target.isNullable)
        }
    }
}