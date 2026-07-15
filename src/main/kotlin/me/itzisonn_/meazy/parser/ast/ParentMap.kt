package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.parser.ast.statement.Program

class ParentMap(program: Program) {
    private val parents: Map<ProgramUnit, ProgramUnit>

    init {
        val parents = hashMapOf<ProgramUnit, ProgramUnit>()

        fun visit(programUnit: ProgramUnit) {
            for (child in programUnit.children) {
                parents[child] = programUnit
                visit(child)
            }
        }

        visit(program)
        this.parents = parents
    }

    operator fun get(programUnit: ProgramUnit) = parents[programUnit]
}

context(parents: ParentMap)
val ProgramUnit.parent get() = parents[this] ?: error("There's no parent for $this")