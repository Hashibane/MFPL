package expression

import expression.BinaryExpression.Operator.*


enum class LanguageType {
    INT, DOUBLE, STRING, TUPLE, UNIT, BOOL, INVALID
}



// Probably should be an interface!
class TypeChecker {
    val variables = mutableMapOf<String, Pair<ASTNode, LanguageType>>()

    internal fun ASTNode.type(): LanguageType =
        when (this) {
            is IntegerConstant -> LanguageType.INT
            is BoolConstant -> LanguageType.BOOL
            is StringConstant -> LanguageType.STRING
            is TupleConstant -> LanguageType.TUPLE
            UnitConstant -> LanguageType.UNIT
            is FunctionNode -> next?.type() ?: current.type()
            is BinaryExpression -> type()
            is VariableAssignment -> LanguageType.UNIT
            is VariableReference -> variables[name]?.second ?: LanguageType.INVALID
            is DoubleConstant -> LanguageType.DOUBLE
        }

    internal fun BinaryExpression.type(): LanguageType =
        when (operator) {
            PLUS
                if left.type() == LanguageType.STRING && right.type() == LanguageType.STRING
                    -> LanguageType.STRING

            PLUS,
            MINUS,
            MULTIPLY,
            DIVIDE
                ->
                    when (left.type() to right.type()) {
                        LanguageType.INT to LanguageType.INT -> LanguageType.INT
                        else -> LanguageType.DOUBLE
                    }

            INDEX
                if (left.type() == LanguageType.TUPLE && right.type() == LanguageType.INT)
                    -> {
                        val rawTuple = (left as TupleConstant).value
                        val rawIndex = (right as IntegerConstant).value
                        rawTuple[rawIndex].type()
                    }

            else -> LanguageType.INVALID
        }



    fun visit(node: ASTNode) {
        with (node) {
            when (this) {
                is Constant -> return
                is FunctionNode -> {
                    visit(current)
                    visit(next ?: return)
                }
                is VariableAssignment -> variables[name] = value
                is VariableReference -> checkVariable(name)
                is BinaryExpression -> {
                    checkBinaryExpression(this)
                    visit(left)
                    visit(right)
                }
            }
        }
    }

    private fun checkBinaryExpression(node: BinaryExpression) {
        with (node) {
            when (operator) {
                PLUS,
                MINUS,
                MULTIPLY,
                DIVIDE
                    -> with (node.left) {
                        when (this) {
                            is BinaryExpression -> checkBinaryExpression(this)
                        }
                    }
                CONCAT
                    -> checkBinaryOperator<StringConstant, StringConstant>(this)

                INDEX
                    -> checkBinaryOperator<TupleConstant, IntegerConstant>(this)
            }
        }

    }

    private inline fun <reified T> shallowTypeCheck(node: ASTNode) =
        require((node is T) || (node is VariableReference && variables[node.name] is T))

    private inline fun <reified T, reified U> checkBinaryOperator(node: BinaryExpression) {
        shallowTypeCheck<T>(node.left)
        shallowTypeCheck<U>(node.right)
        if (node.left is OperatorNode) {

        }
    }

    private fun checkVariable(name: String) =
        variables[name] ?: throw IllegalStateException("Variable with name $name does not exist")

}


