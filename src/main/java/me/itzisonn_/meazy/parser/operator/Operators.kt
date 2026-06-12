package me.itzisonn_.meazy.parser.operator

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.custom.*
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Operators registrar
 * @see Registries.OPERATORS
 */
object Operators {
    private var hasRegistered = false

    val power get() = parseById("power")!!
    val negation get() = parseById("negation")!!
    val inversion get() = parseById("inversion")!!



    /**
     * Finds registered Operator with given symbol and given type
     * 
     * @param symbol Operator's symbol
     * @param operatorType Operator's type or null if any
     * @return Operator with given symbol or null
     */
    fun parse(symbol: String, operatorType: OperatorType?): Operator? {
        for (entry in Registries.OPERATORS.entries) {
            val operator = entry.getValue()
            if (symbol == operator.symbol && (operatorType == null || operator.operatorType == operatorType)) return operator
        }

        return null
    }

    /**
     * Finds registered Operator with given id
     * 
     * @param id Operator's id
     * @return Operator with given id or null
     */
    fun parseById(id: String): Operator? {
        for (entry in Registries.OPERATORS.entries) {
            val operator = entry.getValue()
            if (operator.id == id) return operator
        }

        return null
    }



    /**
     * Initializes [Registries.OPERATORS] registry
     *
     * *Don't use this method because it's called once at [Registries] initialization*
     * 
     * @throws IllegalStateException If [Registries.OPERATORS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "Operators have already been initialized" }
        hasRegistered = true

        register(AdditionOperator())
        register(SubtractionOperator())
        register(MultiplicationOperator())
        register(DivisionOperator())
        register(RemainderOperator())
        register(PowerOperator())
        register(NegationOperator())

        register(AndOperator())
        register(OrOperator())
        register(InversionOperator())
        register(EqualsOperator())
        register(NotEqualsOperator())
        register(GreaterOperator())
        register(GreaterOrEqualsOperator())
        register(LessOperator())
        register(LessOrEqualsOperator())
    }

    private fun register(operator: Operator) {
        Registries.OPERATORS.register(getDefaultIdentifier(operator.id), operator)
    }



    fun produceCompare(
        instructionsSet: InstructionsSet, environment: Environment,
        operatorExpression: OperatorExpression, operation: ComparisonOperation
    ) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment, operatorExpression)
        val rightType = right.getType(environment, operatorExpression)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType == null || rightNumberType == null) {
            error("Can't compare values $leftType and $rightType") //TODO
        }

        if (leftType.isNullable || rightType.isNullable) error("Can't compare nullable numbers")
        val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

        val trueLabel = instructionsSet.createAndInitLabel()
        val endLabel = instructionsSet.createAndInitLabel()

        left.emit(instructionsSet, environment, operatorExpression)
        instructionsSet.convertToNumberType(leftNumberType, commonNumberType)

        right.emit(instructionsSet, environment, operatorExpression)
        instructionsSet.convertToNumberType(rightNumberType, commonNumberType)

        instructionsSet.gotoLabelIfComparisonTrue(commonNumberType, operation, trueLabel)
        instructionsSet.loadConstant(0)
        instructionsSet.gotoLabel(endLabel)

        instructionsSet.bindLabel(trueLabel)
        instructionsSet.loadConstant(1)

        instructionsSet.bindLabel(endLabel)
    }
}
