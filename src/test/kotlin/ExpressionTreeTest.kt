import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionTreeTest {
    private fun createSut(): ExpressionEvaluator {
        val expressionEvaluatorProxy = ExpressionEvaluatorProxy()
        val valueEvaluatorProxy = ValueEvaluatorProxy()
        val valueEvaluatorComposite = ValueEvaluatorComposite(
            constantValueEvaluator = ConstantValueEvaluator()
        ).also { valueEvaluatorProxy.original = it }

        return ExpressionEvaluatorComposite(
            andExpressionEvaluator = AndExpressionEvaluator(expressionEvaluatorProxy),
            orExpressionEvaluator = OrExpressionEvaluator(expressionEvaluatorProxy),
            notExpressionEvaluator = NotExpressionEvaluator(expressionEvaluatorProxy),
            constantExpressionEvaluator = ConstantExpressionEvaluator(),
            equalsExpressionEvaluator = EqualsExpressionEvaluator(valueEvaluatorProxy),
            greaterThanExpressionEvaluator = GreaterThanExpressionEvaluator(valueEvaluatorProxy),
        ).also { expressionEvaluatorProxy.original = it }
    }

    @Test
    fun expressionTest1() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.Constant(true),
            BoolExpression.Constant(true),
            BoolExpression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest2() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.Constant(true),
            BoolExpression.Constant(false),
            BoolExpression.Constant(true),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest3() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.Constant(false),
            BoolExpression.Constant(false),
            BoolExpression.Constant(false),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest4() = runBlocking {
        val expression: BoolExpression = BoolExpression.Or(
            BoolExpression.Constant(true),
            BoolExpression.Constant(true),
            BoolExpression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest5() = runBlocking {
        val expression: BoolExpression = BoolExpression.Or(
            BoolExpression.Constant(true),
            BoolExpression.Constant(false),
            BoolExpression.Constant(true),
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest6() = runBlocking {
        val expression: BoolExpression = BoolExpression.Or(
            BoolExpression.Constant(false),
            BoolExpression.Constant(false),
            BoolExpression.Constant(false),
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest7() = runBlocking {
        val expression: BoolExpression = BoolExpression.Not(
            BoolExpression.Or(
                BoolExpression.Constant(true),
                BoolExpression.Constant(false),
                BoolExpression.Constant(true),
            )
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest8() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.Not(
                BoolExpression.Or(
                    BoolExpression.Constant(true),
                    BoolExpression.Constant(false),
                    BoolExpression.Constant(true),
                )
            ),
            BoolExpression.Constant(true)
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest9() = runBlocking {
        val expression: BoolExpression = BoolExpression.Or(
            BoolExpression.Not(
                BoolExpression.Or(
                    BoolExpression.Constant(true),
                    BoolExpression.Constant(false),
                    BoolExpression.Constant(true),
                )
            ),
            BoolExpression.Constant(true)
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest10() = runBlocking {
        val expression: BoolExpression = BoolExpression.Or(
            BoolExpression.Not(
                BoolExpression.Not(
                    BoolExpression.Or(
                        BoolExpression.Constant(true),
                        BoolExpression.Constant(false),
                        BoolExpression.Constant(true),
                    )
                )
            ),
            BoolExpression.Not(
                BoolExpression.Constant(true),
            )
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest11() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.Equals(
                ValueExpression.Constant(1),
                ValueExpression.Constant(2),
            )
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }

    @Test
    fun expressionTest12() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.GreaterThan(
                ValueExpression.Constant(2),
                ValueExpression.Constant(1),
            )
        )

        val sut = createSut()
        assertEquals(true, sut.evaluate(expression))
    }

    @Test
    fun expressionTest13() = runBlocking {
        val expression: BoolExpression = BoolExpression.And(
            BoolExpression.GreaterThan(
                ValueExpression.Constant(1),
                ValueExpression.Constant(2),
            )
        )

        val sut = createSut()
        assertEquals(false, sut.evaluate(expression))
    }
}
