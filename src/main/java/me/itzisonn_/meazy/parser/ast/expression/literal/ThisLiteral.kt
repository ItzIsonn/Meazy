package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getParentOrSelf
import org.jspecify.annotations.NullMarked
import java.lang.constant.ClassDesc

@NullMarked
class ThisLiteral : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        instructions.loadThisReference()
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val classEnvironment = environment.getParentOrSelf<ClassEnvironment>()
            ?: error("Parent environment for THIS expression must be ClassEnvironment")
        return DataType.ofNonNull(ClassDesc.of(classEnvironment.fullClassName))
    }
}
