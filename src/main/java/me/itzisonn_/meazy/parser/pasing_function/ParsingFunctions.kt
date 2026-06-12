package me.itzisonn_.meazy.parser.pasing_function

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.pasing_function.expression.*
import me.itzisonn_.meazy.parser.pasing_function.statement.*
import me.itzisonn_.meazy.registry.Registries

/**
 * Parsing functions registrar
 * 
 * @see Registries.PARSING_FUNCTIONS
 */
object ParsingFunctions {
    private var hasRegistered = false

    /**
     * Initializes [Registries.PARSING_FUNCTIONS] registry
     *
     * *Don't use this method because it's called once at [Registries] initialization*
     * 
     * @throws IllegalStateException If [Registries.PARSING_FUNCTIONS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "ParsingFunctions have already been initialized" }
        hasRegistered = true

        register(ProgramParsingFunction())

        register(HeaderStatementParsingFunction())
        register(ImportStatementParsingFunction())

        register(GlobalStatementParsingFunction())
        register(ClassDeclarationStatementParsingFunction())
        register(ClassBodyStatementParsingFunction())
        register(InterfaceDeclarationStatementParsingFunction())
        register(InterfaceBodyStatementParsingFunction())
        register(FunctionDeclarationStatementParsingFunction())
        register(VariableDeclarationStatementParsingFunction())
        register(ConstructorDeclarationStatementParsingFunction())
        register(BaseCallStatementParsingFunction())
        register(LocalStatementParsingFunction())
        register(IfStatementParsingFunction())
        register(ForeachStatementParsingFunction())
        register(WhileStatementParsingFunction())
        register(ReturnStatementParsingFunction())
        register(ContinueStatementParsingFunction())
        register(BreakStatementParsingFunction())
        register(AssignmentStatementParsingFunction())
        register(PostfixStatementParsingFunction())

        register(ExpressionParsingFunction())
        register(ListCreationExpressionParsingFunction())
        register(MapCreationExpressionParsingFunction())
        register(NullCheckExpressionParsingFunction())
        register(LogicalExpressionParsingFunction())
        register(ComparisonExpressionParsingFunction())
        register(AdditionExpressionParsingFunction())
        register(MultiplicationExpressionParsingFunction())
        register(PowerExpressionParsingFunction())
        register(InversionExpressionParsingFunction())
        register(IsExpressionParsingFunction())
        register(NegationExpressionParsingFunction())
        register(MemberExpressionParsingFunction())
        register(CallExpressionParsingFunction())
        register(PrimaryExpressionParsingFunction())
    }

    private fun register(parsingFunction: AbstractParsingFunction<out ProgramUnit>) {
        Registries.PARSING_FUNCTIONS.register(getDefaultIdentifier(parsingFunction.id), parsingFunction)
    }
}