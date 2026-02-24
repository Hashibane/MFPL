package expression

import expression.BinaryExpression.BinaryOperator.*

class Interpreter {
    val variables = mutableMapOf<String, ASTNode>()
    val stack = ArrayDeque<ASTNode>()

    fun visit(node: ASTNode) {
        with (node) {
            when (this) {
                is Constant -> stack.add(this)
                is FunctionNode -> {
                    visit(current)
                    visit(next ?: return)
                }
                is VariableAssignment -> variables[name] = value
                // Should not be null, as it was checked by the type checker
                is VariableReference -> variables[name]!!.let { stack.add(it) }
                is BinaryExpression -> {
                    visit(left)
                    visit(right)
                }
            }
        }
    }

    fun handleBinaryExpressions(node: BinaryExpression) {
        with (node) {
           when (operator) {
               PLUS -> {
                   val left = stack.removeLast()
                   val right = stack.removeLast()


               }
               MINUS -> TODO()
               MULTIPLY -> TODO()
               DIVIDE -> TODO()
               CONCAT -> TODO()
           }
        }
    }


}