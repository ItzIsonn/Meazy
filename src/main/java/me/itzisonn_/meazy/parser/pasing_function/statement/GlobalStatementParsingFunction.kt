package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class GlobalStatementParsingFunction : AbstractParsingFunction<Statement>("global_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == `class`) {
            return parser.parse<ClassDeclarationStatement>(
                getDefaultIdentifier("class_declaration_statement"),
                modifiers
            )
        }
        if (parser.current.type == `interface`) {
            return parser.parse<InterfaceDeclarationStatement>(
                getDefaultIdentifier("interface_declaration_statement"),
                modifiers
            )
        }
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
                false
            )
        }

        throw InvalidStatementException(parser.current.line, translatable("meazy:parser.exception.global_statement"))
    }
}
