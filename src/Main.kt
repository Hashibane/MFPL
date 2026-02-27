
import expression.*
import expression.BinaryExpression.*
import expression.BinaryExpression.BinaryOperator.*
import expression.TernaryExpression.TernaryOperator.*

fun main() {
    val t  = TypeChecker()
    val program = listOf<ASTNode>(
        VariableAssignment("fibonacci",
            FunctionDefinition(
                listOf("n" to LanguageType.INT),
                TernaryExpression(
                    condition = BinaryExpression(
                        left = VariableReference("n"),
                        right = IntegerConstant(1),
                        operator = EQ
                    ),
                    left = IntegerConstant(1),
                    right = BinaryExpression(
                        left = FunctionCall(
                            definition = VariableReference("fibonacci"),
                            parameters = listOf(
                                BinaryExpression(
                                    left = VariableReference("n"),
                                    right = IntegerConstant(1),
                                    operator = MINUS
                                )
                            )
                        ),
                        right = FunctionCall(
                            definition = VariableReference("fibonacci"),
                            parameters = listOf(
                                BinaryExpression(
                                    left = VariableReference("n"),
                                    right = IntegerConstant(2),
                                    operator = MINUS
                                )
                            )
                        ),
                        operator = PLUS
                    ),
                    operator = IF
                )
                )
            ),

        FunctionCall(VariableReference("fibonacci"), listOf(IntegerConstant(4)))
    )

    t.checkProgram(program)
}

