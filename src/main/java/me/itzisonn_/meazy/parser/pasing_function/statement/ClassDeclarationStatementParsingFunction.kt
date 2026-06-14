package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.DataType.Companion.ofNullable
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.Parameter
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.*
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers.data
import me.itzisonn_.meazy.parser.modifier.Modifiers.enum
import me.itzisonn_.meazy.parser.modifier.Modifiers.get
import me.itzisonn_.meazy.parser.modifier.Modifiers.operator
import me.itzisonn_.meazy.parser.modifier.Modifiers.private
import me.itzisonn_.meazy.parser.modifier.Modifiers.set
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.util.MiscUtils.generatePrefixedName
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

class ClassDeclarationStatementParsingFunction :
    AbstractParsingFunction<ClassDeclarationStatement>("class_declaration_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ClassDeclarationStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.getModifiersFromExtra(extra).toMutableSet()

        parser.consume(`class`, translatable("meazy:parser.expected.keyword", "class"))
        val id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "class")).value

        val generatedBody = mutableListOf<Statement>()
        if (data in modifiers) {
            generatedBody.addAll(generateDataBody(id, ParsingHelper.parseParameters(context)))
            modifiers.remove(data)
        }

        val baseClasses = mutableSetOf<String>()
        var baseClassesLineNumber = -1

        if (parser.current.type == colon) {
            baseClassesLineNumber = parser.current.line
            do {
                parser.next()
                baseClasses.add(parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value)
            }
            while (parser.current.type == comma)
        }

        if (parser.current.type != leftBrace) {
            return ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody)
        }

        parser.next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "class_body"))

        if (parser.current.type == rightBrace) {
            parser.next()
            return ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody)
        }

        parser.consume(newLine, translatable("meazy:parser.expected", "new_line"))
        parser.skipNewLines()

        val enumIds = LinkedHashMap<String, List<Expression>>()
        if (enum in modifiers) {
            if (!baseClasses.isEmpty()) throw InvalidSyntaxException(
                baseClassesLineNumber,
                translatable("meazy:parser.exception.enums.base_classes")
            )

            var enumId = parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value
            var args = if (parser.current.type == leftParenthesis) ParsingHelper.parseArgs(context)
            else mutableListOf()
            enumIds[enumId] = args

            while (parser.current.type == comma) {
                parser.next()
                parser.skipNewLines()

                val lineNumber = parser.current.line
                enumId = parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value
                if (enumIds.containsKey(enumId)) throw InvalidSyntaxException(
                    lineNumber,
                    translatable("meazy:parser.exception.enums.duplicated_entries")
                )

                args = if (parser.current.type == leftParenthesis) ParsingHelper.parseArgs(context)
                else mutableListOf()
                enumIds[enumId] = args
            }

            parser.skipNewLines()
        }

        val body = generatedBody.toMutableList()
        while (parser.current.type != endOfFile && parser.current.type != rightBrace) {
            val statement = parser.parse<Statement>(getDefaultIdentifier("class_body_statement"))
            body.add(statement)

            if (statement is VariableDeclarationStatement) {
                if (get in statement.modifiers) {
                    body.add(getGetFunction(statement.id, statement.dataType!!))
                }
                if (set in statement.modifiers && !statement.isConstant) {
                    body.add(getSetFunction(statement.id, statement.dataType!!))
                }
            }

            parser.skipNewLines()
        }

        parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "class_body"))
        return ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds)
    }


    companion object {
        private fun generateDataBody(id: String, dataVariables: List<Parameter>): MutableList<Statement> {
            val body: MutableList<Statement> = ArrayList<Statement>()

            for (dataVariable in dataVariables) {
                body.add(
                    VariableDeclarationStatement(
                        setOf(private),
                        dataVariable.isConstant,
                        dataVariable.id,
                        dataVariable.dataType,
                        null
                    )
                )
            }

            val constructorBody: MutableList<LocalStatement> = ArrayList<LocalStatement>()
            for (callArgExpression in dataVariables) {
                constructorBody.add(
                    AssignmentStatement(
                        MemberExpression(
                            ThisLiteral(),
                            VariableIdentifier(callArgExpression.id),
                            false
                        ), VariableIdentifier(callArgExpression.id)
                    )
                )
            }
            body.add(ConstructorDeclarationStatement(mutableSetOf<Modifier>(), dataVariables, constructorBody))

            for (dataVariable in dataVariables) {
                body.add(getGetFunction(dataVariable.id, dataVariable.dataType))
            }

            for (dataVariable in dataVariables) {
                if (dataVariable.isConstant) continue
                body.add(getSetFunction(dataVariable.id, dataVariable.dataType))
            }

            val toStringExpression: Expression = getToStringExpression(id, dataVariables)
            body.add(
                FunctionDeclarationStatement(
                    mutableSetOf<Modifier>(),
                    "toString",
                    mutableListOf<Parameter>(),
                    mutableListOf(ReturnStatement(toStringExpression)),
                    ofNonNull(ConstantDescs.CD_String)
                )
            )

            val copyArgs: MutableList<Expression> = ArrayList<Expression>()
            for (dataVariable in dataVariables) {
                copyArgs.add(VariableIdentifier(dataVariable.id))
            }
            body.add(
                FunctionDeclarationStatement(
                    mutableSetOf<Modifier>(),
                    "copy",
                    mutableListOf<Parameter>(),
                    mutableListOf(ReturnStatement(CallExpression(ClassIdentifier(id), copyArgs))),
                    ofNonNull(ClassDesc.of(id))
                )
            )

            var equalsExpression: Expression?
            if (!dataVariables.isEmpty()) {
                equalsExpression = OperatorExpression(
                    VariableIdentifier(dataVariables.first().id),
                    MemberExpression(
                        VariableIdentifier("value"),
                        CallExpression(
                            FunctionIdentifier(generatePrefixedName("get", dataVariables.first().id)),
                            mutableListOf<Expression>()
                        ),
                        false
                    ),
                    "==", OperatorType.INFIX
                )
                for (i in 1..<dataVariables.size) {
                    val dataVariable = dataVariables.get(i)
                    equalsExpression = OperatorExpression(
                        equalsExpression!!,
                        OperatorExpression(
                            VariableIdentifier(dataVariable.id),
                            MemberExpression(
                                VariableIdentifier("value"),
                                CallExpression(
                                    FunctionIdentifier(generatePrefixedName("get", dataVariable.id)),
                                    mutableListOf<Expression>()
                                ),
                                false
                            ),
                            "==", OperatorType.INFIX
                        ),
                        "&&", OperatorType.INFIX
                    )
                }
            }
            else equalsExpression = BooleanLiteral(true)
            body.add(
                FunctionDeclarationStatement(
                    setOf(operator),
                    "equals",
                    listOf(Parameter("value", ofNullable(ConstantDescs.CD_Object), true)),
                    mutableListOf(
                        IfStatement(
                            OperatorExpression(VariableIdentifier("value"), NullLiteral(), "==", OperatorType.INFIX),
                            listOf(ReturnStatement(BooleanLiteral(false))),
                            null
                        ),
                        IfStatement(
                            OperatorExpression(
                                IsExpression(VariableIdentifier("value"), id, true),
                                null,
                                "!",
                                OperatorType.PREFIX
                            ),
                            listOf(ReturnStatement(BooleanLiteral(false))),
                            null
                        ),
                        ReturnStatement(equalsExpression)
                    ),
                    ofNonNull(ConstantDescs.CD_boolean)
                )
            )

            return body
        }

        private fun getToStringExpression(id: String, dataVariables: List<Parameter>): Expression {
            var toStringExpression: Expression = StringLiteral(id + "(" + (if (dataVariables.isEmpty()) ")" else ""))
            for (i in dataVariables.indices) {
                val dataVariable = dataVariables.get(i)

                val endingExpression: Expression?
                if (i == dataVariables.size - 1) endingExpression = OperatorExpression(
                    VariableIdentifier(dataVariable.id),
                    StringLiteral(")"),
                    "+", OperatorType.INFIX
                )
                else endingExpression = VariableIdentifier(dataVariable.id)

                toStringExpression = OperatorExpression(
                    toStringExpression,
                    OperatorExpression(
                        StringLiteral((if (i == 0) "" else ",") + dataVariable.id + "="),
                        endingExpression,
                        "+", OperatorType.INFIX
                    ),
                    "+", OperatorType.INFIX
                )
            }
            return toStringExpression
        }

        private fun getGetFunction(id: String, dataType: DataType): FunctionDeclarationStatement {
            return FunctionDeclarationStatement(
                mutableSetOf<Modifier>(),
                generatePrefixedName("get", id),
                mutableListOf<Parameter>(),
                mutableListOf(ReturnStatement(VariableIdentifier(id))),
                dataType
            )
        }

        private fun getSetFunction(id: String, dataType: DataType): FunctionDeclarationStatement {
            return FunctionDeclarationStatement(
                mutableSetOf<Modifier>(),
                generatePrefixedName("set", id),
                listOf(Parameter(id, dataType, true)),
                mutableListOf(
                    AssignmentStatement(
                        MemberExpression(
                            ThisLiteral(),
                            VariableIdentifier(id),
                            false
                        ), VariableIdentifier(id)
                    )
                ),
                null
            )
        }
    }
}
