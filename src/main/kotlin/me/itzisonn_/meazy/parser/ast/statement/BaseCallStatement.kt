package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ResolvedConstructor
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.SymbolResolver.resolveConstructor
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getParent
import me.itzisonn_.meazy.runtime.environment.hasParentOrSelf

class BaseCallStatement(val args: List<Expression>) : LocalStatement {
    override val children = args.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        //TODO add support for automatic base calling before return
        require(environment.hasParentOrSelf<ConstructorEnvironment>()) {
            "Parent environment for BASE statement must be ConstructorEnvironment TODO"
        }

        val resolvedConstructor = environment.resolveConstructor()
        instructions.loadThisReference()

        instructions.invokeSuperClass(
            resolvedConstructor.classDesc,
            resolvedConstructor.methodTypeDesc
        ) {
            for (arg in args) {
                arg.emit(this, environment)
            }
        }
    }

    override fun alwaysReturns() = false



    context(parents: ParentMap)
    private fun Environment.resolveConstructor(): ResolvedConstructor {
        val classEnvironment = getParent<ClassEnvironment>()
            ?: error("Can't call super class not inside class")

        val baseClassDesc = classEnvironment.baseClass ?: error("Failed to resolve constructor")
        val baseClassSymbol = getClass(baseClassDesc) ?: error("Failed to resolve constructor")

        return resolveConstructor(baseClassSymbol.environment, args)
    }
}