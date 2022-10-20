sealed interface Expression {
    class And(
        val expressions: List<Expression>
    ) : Expression {
        constructor(vararg expressions: Expression) : this(expressions.toList())
    }

    class Or(
        val expressions: List<Expression>
    ) : Expression {
        constructor(vararg expressions: Expression) : this(expressions.toList())
    }

    class Not(
        val expression: Expression
    ) : Expression

    class Constant(
        val value: Boolean,
    ) : Expression
}
