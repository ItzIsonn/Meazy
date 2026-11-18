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
import me.itzisonn_.meazy.util.version.Version
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class Program(
    val name: String,
    val version: Version,
    body: List<Statement>
) : DeclarationStatement<FileSymbol> {
    val body = body.toList()

    override val children = body.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        if (environment !is GlobalEnvironment) throw RuntimeException("Environment must be global")

        val packageName = body.filterIsInstance<PackageStatement>().firstOrNull()?.name ?: ""
        val className = name.take(1).uppercase() + name.substring(1) + "Mea"

        val fileEnvironment = FileEnvironment(environment, packageName, className)
        val symbol = FileSymbol(packageName, className, fileEnvironment)

        declareSymbol(symbol)
        environment.addFileEnvironment(fileEnvironment)

        for (statement in body) {
            if (statement is ImportStatement) {
                fileEnvironment.addImport(statement.name)
            }
        }

        for (statement in body) {
            if (statement is DeclarationStatement<*>) {
                statement.declare(fileEnvironment)
            }
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun resolve(environment: Environment) {
        for (statement in body) {
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
        for (statement in body) {
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
            for (statement in body) {
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
                    if (!convertPrimitiveOrBoxed(valueType, variableType, fileEnvironment)) {
                        throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
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