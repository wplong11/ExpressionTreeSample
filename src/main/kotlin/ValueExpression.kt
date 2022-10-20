sealed interface ValueExpression<T> {
    class Constant<T>(
        val value: T
    ) : ValueExpression<T>
}
