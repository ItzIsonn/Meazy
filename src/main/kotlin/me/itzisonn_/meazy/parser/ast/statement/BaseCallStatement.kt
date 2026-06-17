package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getParent
import me.itzisonn_.meazy.runtime.environment.hasParentOrSelf
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import kotlin.collections.map

class BaseCallStatement(val args: List<Expression>) : LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        //TODO add support for automatic base calling before return
        require(environment.hasParentOrSelf<ConstructorEnvironment>()) {
            "Parent environment for BASE statement must be ConstructorEnvironment TODO"
        }

        val resolvedConstructor = resolveConstructor(environment)
        instructions.loadThisReference()

        instructions.invokeSuperClass(
            resolvedConstructor.classDesc,
            resolvedConstructor.methodTypeDesc
        ) {
            for (arg in args) {
                arg.emit(this, environment, this@BaseCallStatement)
            }
        }
    }

    override fun alwaysReturns() = false



    private fun resolveConstructor(environment: Environment): ResolvedConstructor {
        val constructorEnvironment = resolveMeazyConstructor(environment)
            ?: error("Failed to resolve constructor")

        if (constructorEnvironment.getParent() !is ClassEnvironment) {
            throw RuntimeException("Can't call super class not inside class")
        }

        val parameters = constructorEnvironment.parameters.map { it.dataType.classDesc }.toList()

        return ResolvedConstructor(
            ClassDesc.of(constructorEnvironment.getParent().fullClassName),
            MethodTypeDesc.of(ConstantDescs.CD_void, parameters)
        )
    }

    private fun resolveMeazyConstructor(environment: Environment): ConstructorEnvironment? {
        val classEnvironment = environment.getParent<ClassEnvironment>()
            ?: error("Can't call super class not inside class")

        val baseClassDesc = classEnvironment.baseClass ?: return null
        val baseClassEnvironment = environment.getClass(baseClassDesc) ?: return null

        val args = args.map { it.getType(environment, this) }
        return baseClassEnvironment.getConstructor(args)
    }


    private class ResolvedConstructor(
        val classDesc: ClassDesc,
        val methodTypeDesc: MethodTypeDesc
    )
}