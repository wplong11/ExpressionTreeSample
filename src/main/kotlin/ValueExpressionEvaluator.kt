interface ValueEvaluator {
    suspend fun <T> evaluate(expression: ValueExpression<T>): T
}

class ValueEvaluatorComposite(
    private val constantValueEvaluator: ConstantValueEvaluator,
) : ValueEvaluator {
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> evaluate(expression: ValueExpression<T>): T {
        val original: ValueEvaluator = when (expression) {
            is ValueExpression.Constant<*> -> constantValueEvaluator
        }

        return original.evaluate(expression) as T
    }
}

class ValueEvaluatorProxy : ValueEvaluator {
    var original: ValueEvaluator? = null

    override suspend fun <T> evaluate(expression: ValueExpression<T>): T {
        return original!!.evaluate(expression)
    }
}

class ConstantValueEvaluator : ValueEvaluator {
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> evaluate(expression: ValueExpression<T>): T {
        require(expression is ValueExpression.Constant<*>) {
            "Given Expression should be ${BoolExpression.Constant::class.simpleName}, actual=${expression::class.java.simpleName}"
        }

        return expression.value as T
    }
}
