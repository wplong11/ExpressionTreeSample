interface ExpressionEvaluator {
    suspend fun evaluate(expression: Expression): Boolean
}

class ExpressionEvaluatorComposite(
    private val andExpressionEvaluator: AndExpressionEvaluator,
    private val orExpressionEvaluator: OrExpressionEvaluator,
    private val notExpressionEvaluator: NotExpressionEvaluator,
    private val constantExpressionEvaluator: ConstantExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: Expression): Boolean {
        val original: ExpressionEvaluator = when (expression) {
            is Expression.And -> andExpressionEvaluator
            is Expression.Or -> orExpressionEvaluator
            is Expression.Not -> notExpressionEvaluator
            is Expression.Constant -> constantExpressionEvaluator
        }

        return original.evaluate(expression)
    }
}

class ExpressionEvaluatorProxy : ExpressionEvaluator {
    var original: ExpressionEvaluator? = null

    override suspend fun evaluate(expression: Expression): Boolean {
        return original!!.evaluate(expression)
    }
}

class AndExpressionEvaluator(
    private val delegate: ExpressionEvaluator,
) : ExpressionEvaluator {
    override suspend fun evaluate(expression: Expression): Boolean {
        require(expression is Expression.And) {
            "Given Expression should be ${Expression.And::class.simpleName}, actual=${expression::class.java.simpleName}"
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
    override suspend fun evaluate(expression: Expression): Boolean {
        require(expression is Expression.Or) {
            "Given Expression should be ${Expression.Or::class.simpleName}, actual=${expression::class.java.simpleName}"
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
    override suspend fun evaluate(expression: Expression): Boolean {
        require(expression is Expression.Not) {
            "Given Expression should be ${Expression.Not::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return !delegate.evaluate(expression.expression)
    }
}

class ConstantExpressionEvaluator : ExpressionEvaluator {
    override suspend fun evaluate(expression: Expression): Boolean {
        require(expression is Expression.Constant) {
            "Given Expression should be ${Expression.Constant::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return expression.value
    }
}
