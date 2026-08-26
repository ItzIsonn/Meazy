package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.parser.ast.statement.DeclarationStatement
import me.itzisonn_.meazy.runtime.data.symbol.Symbol

class SymbolMap {
    private val symbols = mutableMapOf<DeclarationStatement<*>, Symbol>()

    fun <T : Symbol> declare(statement: DeclarationStatement<T>, symbol: T) {
        if (symbols.containsKey(statement)) error("Symbol has already been declared for statement $statement")
        symbols[statement] = symbol
    }

    operator fun get(statement: DeclarationStatement<*>) = symbols[statement]
}

context(symbols: SymbolMap)
val <T : Symbol> DeclarationStatement<T>.symbol: T
    get() {
        @Suppress("UNCHECKED_CAST")
        return (symbols[this] as? T) ?: error("There's no symbol for $this")
    }

context(symbols: SymbolMap)
val DeclarationStatement<*>.hasSymbol get() = symbols[this] != null

context(symbols: SymbolMap)
fun <T : Symbol> DeclarationStatement<T>.declareSymbol(symbol: T) {
    symbols.declare(this, symbol)
}