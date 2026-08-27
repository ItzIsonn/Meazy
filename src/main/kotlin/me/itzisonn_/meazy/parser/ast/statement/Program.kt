package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.FileSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import me.itzisonn_.meazy.util.version.Version
import java.io.File
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class Program(
    file: File?,
    val version: Version,
    private val _body: List<Statement>
) : DeclarationStatement<FileSymbol> {
    val file: File

    override val children = _body.toSet()

    init {
        require(file?.exists() != false) { "File doesn't exist" }
        require(file?.isDirectory() != true) { "File can't be directory" }

        this.file = file ?: File("internal.mea")
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        if (environment !is GlobalEnvironment) throw RuntimeException("Environment must be global")
        val path = file.absolutePath.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }

        var id = file.nameWithoutExtension
        id = id.substring(0, 1).uppercase() + id.substring(1)

        val fileEnvironment = FileEnvironment(
            environment,
            path[path.size - 2],
            id
        )

        val symbol = FileSymbol(
            path[path.size - 2], id,
            fileEnvironment
        )
        declareSymbol(symbol)
        environment.addFileEnvironment(fileEnvironment)

        for (statement in _body) {
            if (statement is ImportStatement) {
                fileEnvironment.addImport(statement.name)
            }
        }

        for (statement in _body) {
            if (statement is DeclarationStatement<*>) {
                statement.declare(fileEnvironment)
            }
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun resolve(environment: Environment) {
        for (statement in _body) {
            if (statement is DeclarationStatement<*>) {
                statement.resolve(symbol.environment)
            }
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val classDesc = ClassDesc.of(symbol.packageName, symbol.className)
        val fileEnvironment = symbol.environment

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
                statement.emit(this, fileEnvironment)
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
                for (variableSymbol in fileEnvironment.variables) {
                    val value = variableSymbol.initializer ?: continue

                    value.emit(this, fileEnvironment)
                    val valueType = value.getType(fileEnvironment).classDesc
                    val variableType = variableSymbol.dataType.classDesc

                    if (!fileEnvironment.isInstanceOf(valueType, variableType)) {
                        if (!convertPrimitiveOrBoxed(valueType, variableType)) {
                            throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
                        }
                    }

                    storeStaticField(
                        classDesc,
                        variableSymbol.id,
                        variableType
                    )
                }

                returnVoid()
            }
        }
    }
}