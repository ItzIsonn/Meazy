package me.itzisonn_.meazy.runtime.data.operator

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.custom.AdditionOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.AndOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.DivisionOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.EqualsOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.GreaterOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.GreaterOrEqualsOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.InversionOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.LessOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.LessOrEqualsOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.MultiplicationOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.NegationOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.NotEqualsOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.OrOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.PowerOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.RemainderOperator
import me.itzisonn_.meazy.runtime.data.operator.custom.SubtractionOperator
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Operators registrar
 */
object Operators {
    private val operators = mutableSetOf<Operator>()
    private var hasInitialized = false

    fun add(operator: Operator) {
        require(get(operator.id) == null) { "Operator with id '${operator.id}' already exists" }
        operators += operator
    }
    fun get(id: String) = operators.find { it.id == id }
    fun getAll() = operators.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Operators have already been initialized" }
        hasInitialized = true

        add(AdditionOperator())
        add(SubtractionOperator())
        add(MultiplicationOperator())
        add(DivisionOperator())
        add(RemainderOperator())
        add(PowerOperator())
        add(NegationOperator())

        add(AndOperator())
        add(OrOperator())
        add(InversionOperator())
        add(EqualsOperator())
        add(NotEqualsOperator())
        add(GreaterOperator())
        add(GreaterOrEqualsOperator())
        add(LessOperator())
        add(LessOrEqualsOperator())
    }



    val power get() = getNonNull("power")
    val negation get() = getNonNull("negation")
    val inversion get() = getNonNull("inversion")

    private fun getNonNull(id: String): Operator {
        return get(id)!!
    }



    /**
     * Finds registered Operator with given symbol and given type
     * 
     * @param symbol Operator's symbol
     * @param operatorType Operator's type or null if any
     * @return Operator with given symbol or null
     */
    fun get(symbol: String, operatorType: OperatorType?): Operator? {
        return operators.find { operator ->
            return@find symbol == operator.symbol && (operatorType == null || operator.operatorType == operatorType)
        }
    }
}



context(parents: ParentMap, symbols: SymbolMap)
fun InstructionsSet.compare(
    environment: Environment, operatorExpression: OperatorExpression, operation: ComparisonOperation
) {
    val left = operatorExpression.left
    val right = operatorExpression.right ?: error("Right side of operator expression is null")

    val leftType = left.getType(environment)
    val rightType = right.getType(environment)

    val leftNumberType = valueOf(leftType.classDesc)
    val rightNumberType = valueOf(rightType.classDesc)

    if (leftNumberType == null || rightNumberType == null) {
        error("Can't compare values $leftType and $rightType") //TODO
    }

    if (leftType.isNullable || rightType.isNullable) error("Can't compare nullable numbers")
    val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

    val trueLabel = createAndInitLabel()
    val endLabel = createAndInitLabel()

    left.emit(this, environment)
    convertToNumberType(leftNumberType, commonNumberType)

    right.emit(this, environment)
    convertToNumberType(rightNumberType, commonNumberType)

    gotoLabelIfComparisonTrue(commonNumberType, operation, trueLabel)
    loadConstant(0)
    gotoLabel(endLabel)

    bindLabel(trueLabel)
    loadConstant(1)

    bindLabel(endLabel)
}