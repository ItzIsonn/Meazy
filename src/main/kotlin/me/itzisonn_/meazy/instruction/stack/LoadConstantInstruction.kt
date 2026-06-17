package me.itzisonn_.meazy.instruction.stack

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.classfile.CodeBuilder
import java.lang.constant.ConstantDesc

class LoadConstantInstruction private constructor(private val block: CodeBuilder.() -> Unit) : Instruction {
    constructor(value: Int) : this({ loadConstant(value) })
    constructor(value: Long) : this({ loadConstant(value) })
    constructor(value: Float) : this({ loadConstant(value) })
    constructor(value: Double) : this({ loadConstant(value) })
    constructor(value: Boolean) : this(if (value) 1 else 0)
    constructor(value: String) : this({ ldc(constantPool().stringEntry(value)) })
    constructor(value: ConstantDesc) : this({ loadConstant(value) })
    constructor(): this({ aconst_null() })

    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.block()
    }
}
