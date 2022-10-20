import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionTreeTest {
    private fun createSut(): ExpressionEvaluator {
        val proxy = ExpressionEvaluatorProxy()
        return ExpressionEvaluatorComposite(
            andExpressionEvaluator = AndExpressionEvaluator(proxy),
            orExpressionEvaluator = OrExpressionEvaluator(proxy),
            notExpressionEvaluator = NotExpressionEvaluator(proxy),
            constantExpressionEvaluator = ConstantExpressionEvaluator(),
        ).also { proxy.original = it }
    }

    @Test
    fun expressionTest1() = runBlocking {
        val expression: Expression = Expression.And(
            Expression.Constant(true),
            Expression.Constant(true),
            Expression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest2() = runBlocking {
        val expression: Expression = Expression.And(
            Expression.Constant(true),
            Expression.Constant(false),
            Expression.Constant(true),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest3() = runBlocking {
        val expression: Expression = Expression.And(
            Expression.Constant(false),
            Expression.Constant(false),
            Expression.Constant(false),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest4() = runBlocking {
        val expression: Expression = Expression.Or(
            Expression.Constant(true),
            Expression.Constant(true),
            Expression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest5() = runBlocking {
        val expression: Expression = Expression.Or(
            Expression.Constant(true),
            Expression.Constant(false),
            Expression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest6() = runBlocking {
        val expression: Expression = Expression.Or(
            Expression.Constant(false),
            Expression.Constant(false),
            Expression.Constant(false),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest7() = runBlocking {
        val expression: Expression = Expression.Not(
            Expression.Or(
                Expression.Constant(true),
                Expression.Constant(false),
                Expression.Constant(true),
            )
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest8() = runBlocking {
        val expression: Expression = Expression.And(
            Expression.Not(
                Expression.Or(
                    Expression.Constant(true),
                    Expression.Constant(false),
                    Expression.Constant(true),
                )
            ),
            Expression.Constant(true)
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest9() = runBlocking {
        val expression: Expression = Expression.Or(
            Expression.Not(
                Expression.Or(
                    Expression.Constant(true),
                    Expression.Constant(false),
                    Expression.Constant(true),
                )
            ),
            Expression.Constant(true)
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest10() = runBlocking {
        val expression: Expression = Expression.Or(
            Expression.Not(
                Expression.Not(
                    Expression.Or(
                        Expression.Constant(true),
                        Expression.Constant(false),
                        Expression.Constant(true),
                    )
                )
            ),
            Expression.Not(
                Expression.Constant(true),
            )
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }
}
