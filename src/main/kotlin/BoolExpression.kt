sealed interface BoolExpression {
    class And(
        val expressions: List<BoolExpression>
    ) : BoolExpression {
        constructor(vararg expressions: BoolExpression) : this(expressions.toList())
    }

    class Or(
        val expressions: List<BoolExpression>
    ) : BoolExpression {
        constructor(vararg expressions: BoolExpression) : this(expressions.toList())
    }

    class Not(
        val expression: BoolExpression
    ) : BoolExpression

    class Constant(
        val value: Boolean,
    ) : BoolExpression

    class Equals<T>(
        val left: ValueExpression<T>,
        val right: ValueExpression<T>,
    ) : BoolExpression

    class GreaterThan<T>(
        val left: ValueExpression<T>,
        val right: ValueExpression<T>,
    ) : BoolExpression
}
