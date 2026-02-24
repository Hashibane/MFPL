package expression

import expression.BinaryExpression.BinaryOperator.*
import expression.UnaryExpression.UnaryOperator.*


enum class LanguageType {
    INT, DOUBLE, STRING, TUPLE, UNIT, BOOL, INVALID,
}


//TODO
/*
    Type should be an extension property
    Environment should be a delegate
 */

// Probably should be an interface!
class TypeChecker {
    val variables = mutableMapOf<String, LanguageType>()

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
            is VariableReference -> variables[name] ?: LanguageType.INVALID
            is DoubleConstant -> LanguageType.DOUBLE
            is UnaryExpression -> type()
        }

    internal fun UnaryExpression.type(): LanguageType =
        when (operator) {
            NOT -> node.type()
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
                -> when (val l = left.type() to right.type()) {
                    LanguageType.INT to LanguageType.INT -> LanguageType.INT
                    LanguageType.INT to LanguageType.DOUBLE -> LanguageType.DOUBLE
                    LanguageType.DOUBLE to LanguageType.INT -> LanguageType.DOUBLE
                    LanguageType.DOUBLE to LanguageType.DOUBLE -> LanguageType.DOUBLE
                    else -> throwTypeMismatch(this, LanguageType.NUMERIC, left.type())
                }

            EQ,
            NEQ,
                ->
                    if (left.type() == right.type())
                        LanguageType.BOOL
                    else
                        LanguageType.INVALID
            LT,
            GT,
            LTE,
            GTE -> when (val l = left.type() to right.type()) {
                LanguageType.INT to LanguageType.DOUBLE,
                LanguageType.DOUBLE to LanguageType.INT -> LanguageType.BOOL
                else if (l.first == l.second) -> LanguageType.BOOL
                else -> LanguageType.INVALID
            }

            AND,
            OR ->
                if (left.type() == LanguageType.BOOL && right.type() == LanguageType.BOOL)
                    LanguageType.BOOL
                else
                    LanguageType.INVALID
        }

    internal fun throwTypeMismatch(expected: LanguageType, received: LanguageType): Nothing {
        throw IllegalStateException("Expected type $expected, received type: $received in node $this")
    }

    fun visit(node: ASTNode) {
        if (node.type() == LanguageType.INVALID) {
            throw IllegalStateException("Node type is invalid!")
        }

        if (node is FunctionNode) visit(node.next ?: return)
    }

}


