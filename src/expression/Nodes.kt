package expression

sealed interface ASTNode
sealed interface Constant : ASTNode // marker interface for constants
sealed interface NumericNode : ASTNode // marker interface for numerics
sealed interface OperatorNode : ASTNode // marker interface for operators

/*
The classes below are concrete implementations of AST nodes. I don't really want to shatter the implementation across
files, since those are mainly just data classes and there won't be a lot of them
*/

// Constants
data object UnitConstant: Constant
data class StringConstant(val value: String): Constant
data class BoolConstant(val value: Boolean): Constant
data class IntegerConstant(val value: Int): NumericNode, Constant
data class DoubleConstant(val value: Double): NumericNode, Constant
data class TupleConstant(val value: List<ASTNode>): Constant

// Functions
data class FunctionNode(val current: ASTNode, val next: ASTNode?): ASTNode

// Operators
data class UnaryExpression(
    val node: ASTNode,
    val operator: UnaryOperator
) : OperatorNode {
    enum class UnaryOperator {
        NOT
    }
}

data class BinaryExpression(
    val left: ASTNode,
    val right: ASTNode,
    val operator: BinaryOperator
) : OperatorNode {
    enum class BinaryOperator {
        PLUS, MINUS, MULTIPLY, DIVIDE,
        EQ, NEQ, LT, GT, LTE, GTE,
        AND, OR
    }
}



// Variables
data class VariableAssignment(val name: String, val value: ASTNode): ASTNode
data class VariableReference(val name: String): ASTNode


