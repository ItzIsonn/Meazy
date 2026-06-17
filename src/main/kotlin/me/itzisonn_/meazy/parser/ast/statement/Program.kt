package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import me.itzisonn_.meazy.version.Version
import java.io.File
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class Program(
    val file: File,
    val version: Version,
    private val _body: List<Statement>
) : DeclarationStatement {
    private lateinit var fileEnvironment: FileEnvironment

    init {
        require(file.exists()) { "File doesn't exist" }
        require(!file.isDirectory()) { "File can't be directory" }
    }

    override fun declare(environment: Environment) {
        if (environment !is GlobalEnvironment) throw RuntimeException("Environment must be global")
        val path = file.absolutePath.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }

        var id = file.nameWithoutExtension
        id = id.substring(0, 1).uppercase() + id.substring(1)

        fileEnvironment = FileEnvironment(environment, path[path.size - 2], id)
        environment.addFileEnvironment(fileEnvironment)

        for (statement in _body) {
            if (statement is ImportStatement) {
                fileEnvironment.addImport(statement.name)
            }
        }

        for (statement in _body) {
            if (statement is DeclarationStatement) {
                statement.declare(fileEnvironment)
            }
        }
    }

    override fun resolve(environment: Environment) {
        for (statement in _body) {
            if (statement is DeclarationStatement) {
                statement.resolve(fileEnvironment)
            }
        }
    }

    fun emit(instructions: InstructionsSet, environment: Environment) {
        val path = file.absolutePath.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }
        var id = file.nameWithoutExtension
        id = id.substring(0, 1).uppercase() + id.substring(1)

        val classDesc = ClassDesc.of(path[path.size - 2], id)

        val attributes = mutableListOf<InnerClassesAttribute>()
        for (statement in _body) {
            if (statement !is ClassDeclarationStatement) continue
            if (Modifiers.private !in statement.modifiers) continue
            attributes.add(statement.getInnerClassesAttribute(fileEnvironment))
        }

        instructions.withClass(
            classDesc,
            ConstantDescs.CD_Object,
            setOf(),
            setOf(AccessFlag.PUBLIC, AccessFlag.FINAL),
            attributes
        ) {
            for (statement in _body) {
                statement.emit(this, fileEnvironment, this@Program)
            }
            withConstructor(
                MethodTypeDesc.of(ConstantDescs.CD_void),
                setOf(AccessFlag.PRIVATE)
            ) {
                loadThisReference()
                invokeSuperClass(
                    ConstantDescs.CD_Object,
                    MethodTypeDesc.of(ConstantDescs.CD_void)
                )
                returnVoid()
            }

            withConstructor(
                MethodTypeDesc.of(ConstantDescs.CD_void),
                setOf(AccessFlag.STATIC)
            ) {
                for (variableValue in fileEnvironment.variables) {
                    val value = variableValue.initializer ?: continue

                    value.emit(this, fileEnvironment, this@Program)
                    val valueType = value.getType(fileEnvironment, this@Program).classDesc
                    val variableType = variableValue.dataType.classDesc

                    if (!fileEnvironment.isInstanceOf(valueType, variableType)) {
                        if (!convertPrimitiveOrBoxed(valueType, variableType)) {
                            throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
                        }
                    }

                    storeStaticField(
                        classDesc,
                        variableValue.id!!,
                        variableType
                    )
                }

                returnVoid()
            }
        }
    }

    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        emit(instructions, environment)
    }
}