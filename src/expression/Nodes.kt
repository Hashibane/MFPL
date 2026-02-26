package expression

sealed interface ASTNode

/*
The classes below are concrete implementations of AST nodes. I don't really want to shatter the implementation across
files, since those are mainly just data classes and there won't be a lot of them
*/

// Constants
data object UnitConstant: ASTNode
data class StringConstant(val value: String): ASTNode
data class BoolConstant(val value: Boolean): ASTNode
data class IntegerConstant(val value: Int): ASTNode
data class DoubleConstant(val value: Double): ASTNode
data class TupleConstant(val value: List<ASTNode>): ASTNode

// Functions
data class FunctionSignature(val typeIdentifiers: List<String>): ASTNode
data class FunctionCall(val node: ASTNode, val arguments: List<ASTNode>): ASTNode
data class FunctionNode(val current: ASTNode, val next: ASTNode?): ASTNode

// Maybe a typealias node would be cool?

// Operators
data class UnaryExpression(
    val node: ASTNode,
    val operator: UnaryOperator
) : ASTNode {
    enum class UnaryOperator {
        NOT
    }
}

data class BinaryExpression(
    val left: ASTNode,
    val right: ASTNode,
    val operator: BinaryOperator
) : ASTNode {
    enum class BinaryOperator {
        PLUS, MINUS, MULTIPLY, DIVIDE,
        EQ, NEQ, LT, GT, LTE, GTE,
        AND, OR
    }
}

data class TernaryExpression(
    val condition: ASTNode,
    val left: ASTNode,
    val right: ASTNode,
    val operator: TernaryOperator
) : ASTNode {
    enum class TernaryOperator {
        IF
    }
}


// Variables
data class VariableAssignment(val name: String, val value: ASTNode): ASTNode
data class VariableReference(val name: String): ASTNode


