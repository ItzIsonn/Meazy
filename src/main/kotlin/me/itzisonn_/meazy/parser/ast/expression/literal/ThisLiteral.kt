package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getParentOrSelf
import java.lang.constant.ClassDesc

class ThisLiteral : Expression {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        instructions.loadThisReference()
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val classEnvironment = environment.getParentOrSelf<ClassEnvironment>()
            ?: error("Parent environment for THIS expression must be ClassEnvironment")
        return DataType.ofNonNull(ClassDesc.of(classEnvironment.fullClassName))
    }
}
