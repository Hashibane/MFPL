package expression

import environment.Environment
import expression.TernaryExpression.TernaryOperator.*
import expression.BinaryExpression.BinaryOperator.*
import expression.UnaryExpression.UnaryOperator.*
import expression.RuntimeObject.Companion.createPrimitiveObject

class Interpreter {
    private val environment = Environment<RuntimeObject>()

    private val ASTNode.value: RuntimeObject
        get() {
            return when (this) {
                is BinaryExpression ->
                    createPrimitiveObject(
                        when (operator) {
                            PLUS -> left.value + right.value
                            MINUS -> left.value - right.value
                            MULTIPLY -> left.value * right.value
                            DIVIDE -> left.value / right.value
                            EQ -> left.value == right.value
                            NEQ -> left.value == right.value
                            LT -> left.value < right.value
                            GT -> left.value > right.value
                            LTE -> left.value <= right.value
                            GTE -> left.value >= right.value
                            AND -> left.value and right.value
                            OR -> right.value or right.value
                        }
                    )

                is BoolConstant -> BoolObject(this.value)
                is DoubleConstant -> DoubleObject(this.value)
                is FunctionCall -> TODO()
                is FunctionDefinition -> TODO()
                is FunctionNode -> {
                    val valueBuffer = current.value
                    next?.value ?: valueBuffer
                }
                is IntegerConstant -> IntegerObject(this.value)
                is StringConstant -> StringObject(this.value)
                is TernaryExpression -> TODO()
                is TupleConstant -> TupleObject(this.value.map { it.value })
                is UnaryExpression -> TODO()
                UnitConstant -> UnitObject
                is VariableAssignment -> TODO()
                is VariableReference -> TODO()
            } as RuntimeObject
        }


}