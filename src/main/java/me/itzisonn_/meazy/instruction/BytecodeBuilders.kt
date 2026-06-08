package me.itzisonn_.meazy.instruction

import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Label
import java.lang.constant.ClassDesc
import java.util.Optional
import java.util.function.Consumer
import kotlin.uuid.Uuid

class BytecodeBuilders private constructor(
    @JvmField val classBuilder: ClassBuilder?,
    @JvmField val codeBuilder: CodeBuilder?,
    private val classes: LinkedHashMap<ClassDesc, ByteArray>,
    private val labels: MutableMap<Uuid, Optional<Label>>
) {
    fun withClass(classDesc: ClassDesc, classBuilder: Consumer<ClassBuilder>) {
        val classFile = ClassFile.of().build(classDesc, classBuilder)
        classes[classDesc] = classFile
    }

    fun getClasses(): MutableMap<ClassDesc, ByteArray> {
        return LinkedHashMap(classes)
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


    @JvmOverloads
    fun copy(classBuilder: ClassBuilder?, codeBuilder: CodeBuilder? = null): BytecodeBuilders {
        return BytecodeBuilders(classBuilder, codeBuilder, classes, labels)
    }

    fun copy(codeBuilder: CodeBuilder?): BytecodeBuilders {
        return copy(classBuilder, codeBuilder)
    }

    companion object {
        @JvmStatic
        fun of(classBuilder: ClassBuilder?, codeBuilder: CodeBuilder?): BytecodeBuilders {
            return BytecodeBuilders(
                classBuilder, codeBuilder,
                LinkedHashMap(), HashMap()
            )
        }
    }
}
