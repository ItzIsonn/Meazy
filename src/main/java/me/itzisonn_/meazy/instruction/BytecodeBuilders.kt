package me.itzisonn_.meazy.instruction

import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Label
import java.lang.constant.ClassDesc
import java.util.Optional
import kotlin.uuid.Uuid

class BytecodeBuilders private constructor(
    val classBuilder: ClassBuilder?,
    val codeBuilder: CodeBuilder?,
    private val classes: LinkedHashMap<ClassDesc, ByteArray>,
    private val labels: MutableMap<Uuid, Optional<Label>>
) {
    fun withClass(classDesc: ClassDesc, classBuilder: ClassBuilder.() -> Unit) {
        val classFile = ClassFile.of().build(classDesc, classBuilder)
        classes[classDesc] = classFile
    }

    fun getClasses(): Map<ClassDesc, ByteArray> {
        return classes.toMap()
    }

    fun getLabel(uuid: Uuid): Label {
        return labels.getOrDefault(uuid, Optional.empty<Label>()).orElseThrow()
    }

    fun hasLabel(uuid: Uuid): Boolean {
        return labels.containsKey(uuid)
    }

    fun hasInitializedLabel(uuid: Uuid): Boolean {
        return labels.getOrDefault(uuid, Optional.empty<Label>()).isPresent
    }

    fun setLabel(uuid: Uuid, label: Label) {
        require(labels.containsKey(uuid)) { "Label with Uuid $uuid does not exist" }
        labels[uuid] = Optional.of<Label>(label)
    }

    fun addLabel(uuid: Uuid) {
        labels[uuid] = Optional.empty<Label>()
    }



    fun copy(classBuilder: ClassBuilder?, codeBuilder: CodeBuilder? = null): BytecodeBuilders {
        return BytecodeBuilders(classBuilder, codeBuilder, classes, labels)
    }

    fun copy(codeBuilder: CodeBuilder?): BytecodeBuilders {
        return copy(classBuilder, codeBuilder)
    }

    companion object {
        fun of(classBuilder: ClassBuilder?, codeBuilder: CodeBuilder?): BytecodeBuilders {
            return BytecodeBuilders(
                classBuilder, codeBuilder,
                LinkedHashMap(), HashMap()
            )
        }
    }
}
