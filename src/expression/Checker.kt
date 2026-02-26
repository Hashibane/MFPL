package expression

import environment.*
import expression.TernaryExpression.TernaryOperator.*
import expression.BinaryExpression.BinaryOperator.*
import expression.UnaryExpression.UnaryOperator.*


private sealed interface LanguageType {
    data object INT : LanguageType
    data object DOUBLE : LanguageType
    data object STRING : LanguageType
    data object TUPLE : LanguageType
    data object UNIT : LanguageType
    data object BOOL : LanguageType
    data class FUNCTION(val arguments: List<LanguageType>, val node: ASTNode) : LanguageType

    data class INVALID(val message: String, val node: ASTNode) : LanguageType {
        companion object {
            fun variableNotExist(variableName: String, node: ASTNode) =
                INVALID("Variable \"$variableName\" does not exist", node)

            fun typeMismatch(prefix: String, types: List<LanguageType>, node: ASTNode) =
                INVALID("$prefix, received types ${types.joinToString()}", node)

            fun typeMismatch(expectedTypes: List<LanguageType>, receivedTypes: List<LanguageType>, node: ASTNode) =
                INVALID("Expected types: " +
                        "${expectedTypes.joinToString()}, received ${receivedTypes.joinToString()}", node)
        }
    }
}


class TypeChecker {
    private val environment = Environment<LanguageType>()

    private fun ASTNode.getType(environment: Environment<LanguageType>): LanguageType = when (this) {
        is IntegerConstant -> LanguageType.INT
        is BoolConstant -> LanguageType.BOOL
        is StringConstant -> LanguageType.STRING
        is TupleConstant -> LanguageType.TUPLE
        UnitConstant -> LanguageType.UNIT
        is FunctionNode -> next?.getType(environment) ?: current.getType(environment)
        is BinaryExpression -> getType(environment)
        is VariableAssignment -> {
            environment[name] = value.getType(Environment(environment))
            LanguageType.UNIT
        }
        is VariableReference ->
            environment[name] ?: LanguageType.INVALID.variableNotExist(name, this)
        is DoubleConstant -> LanguageType.DOUBLE
        is UnaryExpression -> getType(environment)
        is TernaryExpression -> getType(environment)
    }

    private fun UnaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        NOT -> node.getType(environment)
    }

    private fun BinaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        PLUS
            if left.getType(environment) == LanguageType.STRING && right.getType(environment) == LanguageType.STRING
                -> LanguageType.STRING

        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE -> when (val l = left.getType(environment) to right.getType(environment)) {
            LanguageType.INT to LanguageType.INT -> LanguageType.INT
            LanguageType.INT to LanguageType.DOUBLE -> LanguageType.DOUBLE
            LanguageType.DOUBLE to LanguageType.INT -> LanguageType.DOUBLE
            LanguageType.DOUBLE to LanguageType.DOUBLE -> LanguageType.DOUBLE
            else -> LanguageType.INVALID.typeMismatch("Expected types must be numeric", l.toList(), this)
        }

        EQ,
        NEQ -> {
            val leftType = left.getType(environment)
            val rightType = right.getType(environment)
            if (leftType == rightType) {
                LanguageType.BOOL
            } else {
                LanguageType.INVALID.typeMismatch(
                    "Expected types to be the same", listOf(rightType, leftType), this)
            }
        }

        LT,
        GT,
        LTE,
        GTE -> when (val l = left.getType(environment) to right.getType(environment)) {
            LanguageType.INT to LanguageType.DOUBLE,
            LanguageType.DOUBLE to LanguageType.INT -> LanguageType.BOOL
            else if (l.first == l.second) -> LanguageType.BOOL
            else -> LanguageType.INVALID.typeMismatch("Expected types must be numeric or same",
                l.toList(), this)
        }

        AND,
        OR -> when (val l = left.getType(environment) to right.getType(environment)) {
            LanguageType.BOOL to LanguageType.BOOL -> LanguageType.BOOL
            else -> LanguageType.INVALID.typeMismatch(listOf(LanguageType.BOOL,
                LanguageType.BOOL), l.toList(), this)
        }

    }

    private fun TernaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        IF -> {
            val typeCond = condition.getType(environment)
            if (typeCond !is LanguageType.BOOL) {
                LanguageType.INVALID.typeMismatch(
                    listOf(LanguageType.BOOL), listOf(typeCond), this)
            } else {
                // similar code as in BinaryOperator EQ, but not really worth refactor
                val leftType = left.getType(environment)
                val rightType = right.getType(environment)
                if (leftType == rightType) {
                    leftType
                } else {
                    LanguageType.INVALID.typeMismatch(
                        "Expected types to be the same", listOf(rightType, leftType), this)
                }
            }
        }
    }


    fun checkProgram(nodes: List<ASTNode>) {
        for (n in nodes) {
            val t = n.getType(environment)
            if (t is LanguageType.INVALID) {
                throw IllegalStateException("${t.message}, on node $t")
            }
        }
    }

}


