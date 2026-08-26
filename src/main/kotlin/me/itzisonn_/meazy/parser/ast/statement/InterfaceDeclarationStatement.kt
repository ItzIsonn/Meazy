package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import java.lang.classfile.attribute.InnerClassInfo
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.reflect.AccessFlag
import java.util.Optional

class InterfaceDeclarationStatement(
    modifiers: Set<Modifier>,
    val id: String,
    val baseClasses: Set<String>,
    val body: List<Statement>
) : ModifierStatement(modifiers), DeclarationStatement<ClassSymbol> {
    override val children = body.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        require(environment is ClassDeclarationEnvironment) { "Environment must be file TODO" }

        val isInner = Modifiers.private in modifiers

        val classEnvironment = ClassEnvironment(
            environment,
            isInner || Modifiers.shared in modifiers,
            true,
            id,
            baseClasses,
            modifiers
        )

        val symbol = ClassSymbol(
            id, true, baseClasses,
            modifiers, classEnvironment
        )
        declareSymbol(symbol)
        environment.declareClass(symbol)

        for (statement in body) {
            if (statement is DeclarationStatement<*>) {
                statement.declare(classEnvironment)
            }
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
        flags.add(AccessFlag.INTERFACE)
        flags.add(AccessFlag.ABSTRACT)

        if (isInner) attributes.add(getInnerClassesAttribute(environment))
        else {
            if (Modifiers.private in modifiers) flags.add(AccessFlag.PRIVATE)
            else flags.add(AccessFlag.PUBLIC)
        }

        val classEnvironment = symbol.environment

        instructions.withClass(
            classDesc,
            null,
            classEnvironment.interfaces,
            flags,
            attributes
        ) {
            for (statement in body) {
                statement.emit(this, classEnvironment)
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
                AccessFlag.PRIVATE, AccessFlag.STATIC
            )
        )
    }
}
