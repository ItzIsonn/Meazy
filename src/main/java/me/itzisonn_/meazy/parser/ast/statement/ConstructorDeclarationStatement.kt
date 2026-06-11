package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.Parameter
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class ConstructorDeclarationStatement(
    modifiers: Set<Modifier>,
    val parameters: List<Parameter>,
    val body: MutableList<LocalStatement>
) : ModifierStatement(modifiers), DeclarationStatement {
    private lateinit var constructorEnvironment: ConstructorEnvironment

    override fun declare(environment: Environment) {
        if (environment !is ConstructorDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE CONSTRUCTOR HERE TODO")
        }

        val constructorEnvironment = ConstructorEnvironment(
            environment, null, null, modifiers, parameters
        )

        environment.declareConstructor(constructorEnvironment)
        this.constructorEnvironment = constructorEnvironment

        var alwaysReturns = false
        var hasBaseCall = false

        for (localStatement in body) {
            if (localStatement.alwaysReturns()) alwaysReturns = true
            if (localStatement is BaseCallStatement) hasBaseCall = true
        }

        if (!hasBaseCall) body.addFirst(BaseCallStatement(listOf()))
        if (!alwaysReturns) body.add(ReturnStatement(null))
    }

    override fun resolve(environment: Environment) {
        constructorEnvironment.parameters.forEach { it.dataType.resolve(environment) }
    }

    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val startLabel = instructions.createLabel()
        val endLabel = instructions.createLabel()
        constructorEnvironment.setStartLabel(startLabel)
        constructorEnvironment.setEndLabel(endLabel)

        val methodTypeDesc = MethodTypeDesc.of(
            ConstantDescs.CD_void,
            constructorEnvironment.parameters.map { it.dataType.classDesc }.toList()
        )

        val accessFlags = mutableSetOf<AccessFlag>()
        if (constructorEnvironment.modifiers.contains(Modifiers.PRIVATE())) accessFlags.add(AccessFlag.PRIVATE)
        else if (constructorEnvironment.modifiers.contains(Modifiers.PROTECTED())) accessFlags.add(AccessFlag.PROTECTED)
        else accessFlags.add(AccessFlag.PUBLIC)

        instructions.withConstructor(
            methodTypeDesc,
            accessFlags
        ) {
            initLabel(startLabel)
            initLabel(endLabel)

            for (parameter in constructorEnvironment.parameters) {
                val parameterValue = constructorEnvironment.declareVariable(
                    parameter.id,
                    parameter.dataType,
                    parameter.isConstant,
                    null
                )

                setLocalName(
                    parameterValue.slot,
                    parameter.id,
                    parameter.dataType.classDesc,
                    startLabel,
                    endLabel
                )
            }

            bindLabel(startLabel)
            for (statement in body) {
                statement.emit(this, constructorEnvironment, this@ConstructorDeclarationStatement)
            }
            bindLabel(endLabel)
        }
    }
}
