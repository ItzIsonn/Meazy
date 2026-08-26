package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.data.symbol.ConstructorSymbol
import me.itzisonn_.meazy.runtime.environment.*
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import java.lang.classfile.attribute.InnerClassInfo
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag
import java.util.Optional

class ClassDeclarationStatement(
    modifiers: Set<Modifier>,
    val id: String,
    val baseClasses: Set<String>,
    val body: List<Statement>,
    val enumIds: Map<String, List<Expression>> = mapOf()
) : ModifierStatement(modifiers), DeclarationStatement<ClassSymbol> {
    override val children = body.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        require(environment is ClassDeclarationEnvironment) { "Environment must be file TODO" }

        val isInner = Modifiers.private in modifiers

        val classEnvironment = ClassEnvironment(
            environment,
            isInner || Modifiers.shared in modifiers,
            false,
            id,
            baseClasses,
            modifiers
        )

        val symbol = ClassSymbol(
            id, false, baseClasses,
            modifiers, classEnvironment
        )
        declareSymbol(symbol)
        environment.declareClass(symbol)

        for (statement in body) {
            if (statement is DeclarationStatement<*>) {
                statement.declare(classEnvironment)
            }
        }

        if (!classEnvironment.hasConstructor()) {
            classEnvironment.declareConstructor(
                ConstructorSymbol(
                    listOf(), setOf(),
                    ConstructorEnvironment(
                        classEnvironment, null, null, setOf(), listOf()
                    )
                )
            )
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun resolve(environment: Environment) {
        val classEnvironment = symbol.environment
        classEnvironment.resolveBaseClasses()

        for (statement in body) {
            if (statement is DeclarationStatement<*>) {
                statement.resolve(classEnvironment)
            }
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        require(environment is FileEnvironment) { "Environment must be file TODO" }
        val isInner = Modifiers.private in modifiers

        val classDesc = if (isInner) ClassDesc.of(environment.packageName + "." + environment.className + "$" + id)
        else ClassDesc.of(environment.packageName, id)

        val attributes = mutableListOf<InnerClassesAttribute>()
        val flags = mutableSetOf<AccessFlag>()

        if (isInner) attributes.add(getInnerClassesAttribute(environment))
        else {
            if (Modifiers.private in modifiers) flags.add(AccessFlag.PRIVATE)
            else flags.add(AccessFlag.PUBLIC)

            if (Modifiers.abstract in modifiers) flags.add(AccessFlag.ABSTRACT)
            else if (Modifiers.open !in modifiers) flags.add(AccessFlag.FINAL)
        }

        val classEnvironment = symbol.environment

        instructions.withClass(
            classDesc,
            classEnvironment.baseClass,
            classEnvironment.interfaces,
            flags,
            attributes
        ) {
            var hasConstructor = false
            for (statement in body) {
                if (statement is ConstructorDeclarationStatement) hasConstructor = true
                statement.emit(this, classEnvironment)
            }

            if (!hasConstructor) withConstructor(
                MethodTypeDesc.of(ConstantDescs.CD_void),
                setOf(AccessFlag.PUBLIC)
            ) {
                loadThisReference()
                val baseClass = classEnvironment.baseClass
                    ?: error("Class " + classEnvironment.id + " has no base class")

                invokeSuperClass(
                    baseClass,
                    MethodTypeDesc.of(ConstantDescs.CD_void)
                )

                for (variableValue in classEnvironment.variables) {
                    val value = variableValue.initializer ?: continue

                    loadThisReference()
                    value.emit(this, classEnvironment)

                    val valueType = value.getType(classEnvironment).classDesc
                    val variableType = variableValue.dataType.classDesc

                    if (!classEnvironment.isInstanceOf(valueType, variableType)) {
                        if (!convertPrimitiveOrBoxed(valueType, variableType)) {
                            throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
                        }
                    }

                    storeField(
                        classDesc,
                        variableValue.id!!,
                        variableValue.dataType.classDesc
                    )
                }

                returnVoid()
            }
        }
    }

    fun getInnerClassesAttribute(fileEnvironment: FileEnvironment): InnerClassesAttribute {
        val outerClassId = fileEnvironment.fullClassName
        val outerClassDesc = ClassDesc.of(outerClassId)
        val innerClassDesc = ClassDesc.of("$outerClassId$$id")

        return InnerClassesAttribute.of(
            InnerClassInfo.of(
                innerClassDesc,
                Optional.of(outerClassDesc),
                Optional.of(id),
                AccessFlag.PRIVATE, AccessFlag.STATIC, AccessFlag.FINAL
            )
        )
    }
}
