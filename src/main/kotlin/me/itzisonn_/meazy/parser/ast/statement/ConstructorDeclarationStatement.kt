package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ConstructorSymbol
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class ConstructorDeclarationStatement(
    modifiers: Set<Modifier>,
    val parameters: List<Parameter>,
    body: List<LocalStatement>
) : ModifierStatement(modifiers), DeclarationStatement<ConstructorSymbol> {
    val body = body.toMutableList()

    override val children = body.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        if (environment !is ConstructorDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE CONSTRUCTOR HERE TODO")
        }

        val constructorEnvironment = ConstructorEnvironment(
            environment, null, null, modifiers, parameters
        )

        val symbol = ConstructorSymbol(parameters, modifiers, constructorEnvironment)
        declareSymbol(symbol)
        environment.declareConstructor(symbol)

        var alwaysReturns = false
        var hasBaseCall = false

        for (localStatement in body) {
            if (localStatement.alwaysReturns()) alwaysReturns = true
            if (localStatement is BaseCallStatement) hasBaseCall = true
        }

        if (!hasBaseCall) body.addFirst(BaseCallStatement(listOf()))
        if (!alwaysReturns) body.add(ReturnStatement(null))
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun resolve(environment: Environment) {
        symbol.environment.parameters.forEach { it.dataType.resolve(environment) }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val constructorEnvironment = symbol.environment

        val startLabel = instructions.createLabel()
        val endLabel = instructions.createLabel()
        constructorEnvironment.setStartLabel(startLabel)
        constructorEnvironment.setEndLabel(endLabel)

        val methodTypeDesc = MethodTypeDesc.of(
            ConstantDescs.CD_void,
            constructorEnvironment.parameters.map { it.dataType.classDesc }.toList()
        )

        val accessFlags = mutableSetOf<AccessFlag>()
        if (Modifiers.private in constructorEnvironment.modifiers) accessFlags.add(AccessFlag.PRIVATE)
        else if (Modifiers.protected in constructorEnvironment.modifiers) accessFlags.add(AccessFlag.PROTECTED)
        else accessFlags.add(AccessFlag.PUBLIC)

        instructions.withConstructor(
            methodTypeDesc,
            accessFlags
        ) {
            initLabel(startLabel)
            initLabel(endLabel)

            for ((id, dataType, isConstant) in constructorEnvironment.parameters) {
                val parameterValue = constructorEnvironment.declareVariable(
                    id,
                    dataType,
                    isConstant,
                    null,
                    setOf()
                )

                setLocalName(
                    parameterValue.slot,
                    id,
                    dataType.classDesc,
                    startLabel,
                    endLabel
                )
            }

            bindLabel(startLabel)
            for (statement in body) {
                statement.emit(this, constructorEnvironment)
            }
            bindLabel(endLabel)
        }
    }
}
