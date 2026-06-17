package me.itzisonn_.meazy.instruction

import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Label
import java.lang.constant.ClassDesc
import kotlin.uuid.Uuid

class BytecodeBuilders private constructor(
    val classBuilder: ClassBuilder?,
    val codeBuilder: CodeBuilder?,
    private val _classes: MutableMap<ClassDesc, ByteArray>,
    private val labels: MutableMap<Uuid, Label>,
    private val uninitializedLabels: MutableSet<Uuid>
) {
    val classes get() = _classes.toMap()

    fun withClass(classDesc: ClassDesc, classBuilder: ClassBuilder.() -> Unit) {
        val classFile = ClassFile.of().build(classDesc, classBuilder)
        _classes[classDesc] = classFile
    }



    fun getLabel(uuid: Uuid): Label {
        return labels[uuid] ?: error("There's no label with id $uuid")
    }

    fun setLabel(uuid: Uuid, label: Label) {
        require(uuid in uninitializedLabels) { "Label with id $uuid does not exist" }
        uninitializedLabels -= uuid
        labels[uuid] = label
    }

    fun addLabel(uuid: Uuid) {
        uninitializedLabels += uuid
    }



    fun copy(classBuilder: ClassBuilder, codeBuilder: CodeBuilder? = null): BytecodeBuilders {
        return BytecodeBuilders(classBuilder, codeBuilder, _classes, labels, uninitializedLabels)
    }

    fun copy(codeBuilder: CodeBuilder): BytecodeBuilders {
        return BytecodeBuilders(classBuilder, codeBuilder, _classes, labels, uninitializedLabels)
    }

    companion object {
        fun of(classBuilder: ClassBuilder?, codeBuilder: CodeBuilder?): BytecodeBuilders {
            return BytecodeBuilders(
                classBuilder, codeBuilder,
                mutableMapOf(), mutableMapOf(), mutableSetOf()
            )
        }
    }
}
