package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNullable
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.*
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers.enum
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers.get
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers.operator
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers.private
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers.set
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseArgs
import me.itzisonn_.meazy.parser.parsing.parseParameters
import me.itzisonn_.meazy.util.text.translatable
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

object ClassDeclarationStatementParsingFunction : ParsingFunction<ClassDeclarationStatement, Set<Modifier>> {
    override fun Parser.parse(data: Set<Modifier>): ClassDeclarationStatement {
        val modifiers = data.toMutableSet()

        consume(`class`, translatable("meazy:parser.expected.keyword", "class"))
        val classId = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "class")).value

        val generatedBody = mutableListOf<Statement>()
        if (Modifiers.data in modifiers) {
            generatedBody.addAll(generateDataBody(classId, parseParameters()))
            modifiers.remove(Modifiers.data)
        }

        val baseClasses = mutableSetOf<String>()

        if (isNext(colon)) {
            if (enum in modifiers) throw InvalidSyntaxException(
                translatable("meazy:parser.exception.enums.base_classes")
            )

            consume(colon, null)
            baseClasses.add(consume(id, translatable("meazy:parser.expected", "id")).value)

            while (isNext(comma)) {
                consume(comma, null)
                baseClasses.add(consume(id, translatable("meazy:parser.expected", "id")).value)
            }
        }

        if (!isNext(leftBrace)) {
            return ClassDeclarationStatement(modifiers, classId, baseClasses, generatedBody)
        }

        consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "class_body"))

        if (isNext(rightBrace)) {
            consume(rightBrace, null)
            return ClassDeclarationStatement(modifiers, classId, baseClasses, generatedBody)
        }

        consume(newLine, translatable("meazy:parser.expected", "new_line"))

        val enumIds = mutableMapOf<String, List<Expression>>()
        if (enum in modifiers) {
            var enumId = consume(id, translatable("meazy:parser.expected", "id")).value
            var args = if (isNext(leftParenthesis)) parseArgs() else mutableListOf()
            enumIds[enumId] = args

            while (isNext(comma)) {
                consume(comma, null)

                enumId = consume(id, translatable("meazy:parser.expected", "id")).value
                if (enumIds.containsKey(enumId)) throw InvalidSyntaxException(
                    translatable("meazy:parser.exception.enums.duplicated_entries")
                )

                args = if (isNext(leftParenthesis)) parseArgs() else mutableListOf()
                enumIds[enumId] = args
            }
        }

        val body = generatedBody.toMutableList()
        while (!isEndOfFile() && !isNext(rightBrace)) {
            val statement = parse(ClassBodyStatementParsingFunction)
            body.add(statement)

            if (statement is VariableDeclarationStatement) {
                if (get in statement.modifiers) {
                    body.add(getGetFunction(statement.id, statement.dataType!!))
                }
                if (set in statement.modifiers && !statement.isConstant) {
                    body.add(getSetFunction(statement.id, statement.dataType!!))
                }
            }
        }

        consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "class_body"))
        return ClassDeclarationStatement(modifiers, classId, baseClasses, body, enumIds)
    }



    private fun generateDataBody(id: String, dataVariables: List<Parameter>): MutableList<Statement> {
        val body = mutableListOf<Statement>()

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

        val constructorBody = mutableListOf<LocalStatement>()
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
        body.add(ConstructorDeclarationStatement(mutableSetOf(), dataVariables, constructorBody))

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
                mutableSetOf(),
                "toString",
                mutableListOf(),
                mutableListOf(ReturnStatement(toStringExpression)),
                ofNonNull(ConstantDescs.CD_String)
            )
        )

        val copyArgs = mutableListOf<Expression>()
        for (dataVariable in dataVariables) {
            copyArgs.add(VariableIdentifier(dataVariable.id))
        }
        body.add(
            FunctionDeclarationStatement(
                mutableSetOf(),
                "copy",
                mutableListOf(),
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
                        mutableListOf()
                    ),
                    false
                ),
                "==", OperatorType.INFIX
            )
            for (i in 1..<dataVariables.size) {
                val dataVariable = dataVariables[i]
                equalsExpression = OperatorExpression(
                    equalsExpression!!,
                    OperatorExpression(
                        VariableIdentifier(dataVariable.id),
                        MemberExpression(
                            VariableIdentifier("value"),
                            CallExpression(
                                FunctionIdentifier(generatePrefixedName("get", dataVariable.id)),
                                mutableListOf()
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
            val dataVariable = dataVariables[i]

            val endingExpression = if (i == dataVariables.size - 1) OperatorExpression(
                VariableIdentifier(dataVariable.id),
                StringLiteral(")"),
                "+", OperatorType.INFIX
            )
            else VariableIdentifier(dataVariable.id)

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
            mutableSetOf(),
            generatePrefixedName("get", id),
            mutableListOf(),
            mutableListOf(ReturnStatement(VariableIdentifier(id))),
            dataType
        )
    }

    private fun getSetFunction(id: String, dataType: DataType): FunctionDeclarationStatement {
        return FunctionDeclarationStatement(
            mutableSetOf(),
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

fun generatePrefixedName(prefix: String, name: String): String {
    if (name == name.uppercase()) return prefix.uppercase() + "_" + name
    return prefix + name.substring(0, 1).uppercase() + name.substring(1)
}