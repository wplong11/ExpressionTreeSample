import kotlin.math.exp

interface ExpressionEvaluator {
    suspend fun evaluate(expression: BoolExpression): Boolean
}

class ExpressionEvaluatorComposite(
    private val andExpressionEvaluator: AndExpressionEvaluator,
    private val orExpressionEvaluator: OrExpressionEvaluator,
    private val notExpressionEvaluator: NotExpressionEvaluator,
    private val constantExpressionEvaluator: ConstantExpressionEvaluator,
    private val equalsExpressionEvaluator: EqualsExpressionEvaluator,
    private val greaterThanExpressionEvaluator: GreaterThanExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        val original: ExpressionEvaluator = when (expression) {
            is BoolExpression.And -> andExpressionEvaluator
            is BoolExpression.Or -> orExpressionEvaluator
            is BoolExpression.Not -> notExpressionEvaluator
            is BoolExpression.Constant -> constantExpressionEvaluator
            is BoolExpression.Equals<*> -> equalsExpressionEvaluator
            is BoolExpression.GreaterThan<*> -> greaterThanExpressionEvaluator
        }

        return original.evaluate(expression)
    }
}

class ExpressionEvaluatorProxy : ExpressionEvaluator {
    var original: ExpressionEvaluator? = null

    override suspend fun evaluate(expression: BoolExpression): Boolean {
        return original!!.evaluate(expression)
    }
}

class AndExpressionEvaluator(
    private val delegate: ExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.And) {
            "Given Expression should be ${BoolExpression.And::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        for (innerExpression in expression.expressions) {
            if (!delegate.evaluate(innerExpression)) {
                return false
            }
        }

        return true
    }
}

class OrExpressionEvaluator(
    private val delegate: ExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.Or) {
            "Given Expression should be ${BoolExpression.Or::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        for (innerExpression in expression.expressions) {
            if (delegate.evaluate(innerExpression)) {
                return true
            }
        }

        return false
    }
}

class NotExpressionEvaluator(
    private val delegate: ExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.Not) {
            "Given Expression should be ${BoolExpression.Not::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return !delegate.evaluate(expression.expression)
    }
}

class ConstantExpressionEvaluator : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.Constant) {
            "Given Expression should be ${BoolExpression.Constant::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return expression.value
    }
}

class EqualsExpressionEvaluator(
    private val valueEvaluator: ValueEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.Equals<*>) {
            "Given Expression should be ${BoolExpression.Equals::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return valueEvaluator.evaluate(expression.left) == valueEvaluator.evaluate(expression.right)
    }
}

class GreaterThanExpressionEvaluator(
    private val valueEvaluator: ValueEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: BoolExpression): Boolean {
        require(expression is BoolExpression.GreaterThan<*>) {
            "Given Expression should be ${BoolExpression.GreaterThan::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        val lhs = valueEvaluator.evaluate(expression.left)
        val rhs = valueEvaluator.evaluate(expression.right)
        return (lhs as Comparable<Any?>).compareTo(rhs) != -1
    }
}
