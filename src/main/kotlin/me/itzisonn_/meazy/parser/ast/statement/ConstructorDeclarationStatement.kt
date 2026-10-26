package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.parser.cfg.CfgBuilder
import me.itzisonn_.meazy.parser.cfg.ConstructorAnalyzer
import me.itzisonn_.meazy.parser.cfg.ConstructorNormalizer
import me.itzisonn_.meazy.parser.cfg.MissingReturnAnalyzer
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

        ConstructorNormalizer.normalize(this, true)
        val cfg = CfgBuilder().build(body)

        val constructorAnalyzer = ConstructorAnalyzer(cfg)
        val errors = constructorAnalyzer.analyze()
        if (errors.isNotEmpty()) {
            error(errors.joinToString("\n"))
        }

        val missingReturnAnalyzer = MissingReturnAnalyzer(cfg)
        if (missingReturnAnalyzer.hasMissingReturn()) {
            body.add(ReturnStatement(null))
        }
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
