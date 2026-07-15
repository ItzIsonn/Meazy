package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.parser.ast.statement.IfStatement
import me.itzisonn_.meazy.parser.ast.statement.Program

class ParentMap(program: Program) {
    private val parents: Map<ProgramUnit, ProgramUnit>

    init {
        val parents = mutableMapOf<ProgramUnit, ProgramUnit>()

        fun visit(programUnit: ProgramUnit) {
            for (child in programUnit.children) {
                parents[child] = programUnit

                if (child is IfStatement) {
                    var ifStatement: IfStatement? = child.elseStatement
                    while (ifStatement != null) {
                        parents[ifStatement] = programUnit
                        visit(ifStatement)
                        ifStatement = ifStatement.elseStatement
                    }
                }

                visit(child)
            }
        }

        visit(program)
        this.parents = parents.toMap()
    }

    operator fun get(programUnit: ProgramUnit) = parents[programUnit]
}

context(parents: ParentMap)
val ProgramUnit.parent get() = parents[this] ?: error("There's no parent for $this")