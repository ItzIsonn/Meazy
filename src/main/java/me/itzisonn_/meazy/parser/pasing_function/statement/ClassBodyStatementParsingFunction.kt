package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class ClassBodyStatementParsingFunction : AbstractParsingFunction<Statement>("class_body_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == function) {
            return parser.parse<FunctionDeclarationStatement>(
                getDefaultIdentifier("function_declaration_statement"),
                modifiers,
                false
            )
        }
        if (parser.current.type == variable) {
            return parser.parse<VariableDeclarationStatement>(
                getDefaultIdentifier("variable_declaration_statement"),
                modifiers,
                true
            )
        }
        if (parser.current.type == constructor) {
            return parser.parse<ConstructorDeclarationStatement>(
                getDefaultIdentifier("constructor_declaration_statement"),
                modifiers
            )
        }

        throw InvalidStatementException(
            parser.current.line,
            translatable("meazy:parser.expected.statement", "class_body")
        )
    }
}
