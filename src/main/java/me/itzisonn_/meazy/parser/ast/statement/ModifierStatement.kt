package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.parser.modifier.Modifier

/**
 * Represents statement that can have modifiers applied to it
 * @param modifiers ModifierStatement's modifiers
 */
abstract class ModifierStatement(val modifiers: Set<Modifier>) : Statement
