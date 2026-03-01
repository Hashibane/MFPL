package expression

import environment.*
import expression.TernaryExpression.TernaryOperator.*
import expression.BinaryExpression.BinaryOperator.*
import expression.LanguageType.*
import expression.UnaryExpression.UnaryOperator.*


class TypeChecker {
    private val environment = Environment<LanguageType>()

    private fun ASTNode.getType(environment: Environment<LanguageType>): LanguageType = when (this) {
        is IntegerConstant -> INT
        is BoolConstant -> BOOL
        is StringConstant -> STRING
        is TupleConstant -> TUPLE
        UnitConstant -> UNIT
        is FunctionNode -> next?.getType(environment) ?: current.getType(environment)
        is BinaryExpression -> getType(environment)
        is VariableAssignment ->
            // Need special treatment, because of recursion
            if (value is FunctionDefinition) {
                val declaredType = FUNCTION(value.arguments.map { it.second }, value.returnType)
                environment[name] = declaredType

                val inferredType = value.getType(environment)
                if (declaredType != inferredType) {
                    return INVALID.typeMismatch(listOf(declaredType), listOf(inferredType), this)
                }
                inferredType
            } else {
                environment[name] = value.getType(environment)
                UNIT
            }

        is VariableReference ->
            environment[name] ?: INVALID.variableNotExist(name, this)
        is DoubleConstant -> DOUBLE
        is UnaryExpression -> getType(environment)
        is TernaryExpression -> getType(environment)
        is FunctionCall -> {

            fun resolveFunctionCall(function: FUNCTION, parameters: List<ASTNode>): LanguageType  {
                val (inputTypes, returnType) = function

                return when {
                    parameters.size > inputTypes.size ->
                        INVALID.invalidArgumentCount(
                            inputTypes.size,
                            parameters.size, this
                        )

                    parameters.zip(inputTypes).any { (l, r) -> l.getType(environment) != r } ->
                        INVALID.typeMismatch(
                            inputTypes,
                            parameters.map { it.getType(environment) }, this
                        )

                    parameters.size == inputTypes.size -> returnType

                    else -> FUNCTION(inputTypes.drop(parameters.size), returnType)
                }
            }



            when (definition) {
                is FunctionDefinition -> resolveFunctionCall(definition.getType(environment), parameters)
                is VariableReference ->
                    when (val resolvedType = environment[definition.name] ) {
                        null -> INVALID.variableNotExist(definition.name, this)
                        is FUNCTION -> resolveFunctionCall(resolvedType, parameters)
                        else -> INVALID.typeMismatch("Expected a function, received",
                            listOf(resolvedType), this)
                    }

                else -> return INVALID.typeMismatch("Expected a function, received",
                    listOf(definition.getType(environment)), this)
            }
        }
        is FunctionDefinition -> getType(environment)
    }


    private fun FunctionDefinition.getType(environment: Environment<LanguageType>): FUNCTION {
        with(Environment(environment)) {
            this@getType.arguments.forEach { this[it.first] = it.second }
            return FUNCTION(arguments.map { it.second }, node.getType(this))
        }
    }




    private fun UnaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        NOT -> node.getType(environment)
    }

    private fun BinaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        PLUS
            if left.getType(environment) == STRING && right.getType(environment) == STRING
                -> STRING

        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE -> when (val l = left.getType(environment) to right.getType(environment)) {
            INT to INT -> INT
            INT to DOUBLE -> DOUBLE
            DOUBLE to INT -> DOUBLE
            DOUBLE to DOUBLE -> DOUBLE
            else -> INVALID.typeMismatch("Expected types must be numeric", l.toList(), this)
        }

        EQ,
        NEQ -> {
            val leftType = left.getType(environment)
            val rightType = right.getType(environment)
            if (leftType == rightType) {
                BOOL
            } else {
                INVALID.typeMismatch(
                    "Expected types to be the same", listOf(rightType, leftType), this)
            }
        }

        LT,
        GT,
        LTE,
        GTE -> when (val l = left.getType(environment) to right.getType(environment)) {
            INT to DOUBLE,
            DOUBLE to INT -> BOOL
            else if (l.first == l.second) -> BOOL
            else -> INVALID.typeMismatch("Expected types must be numeric or same",
                l.toList(), this)
        }

        AND,
        OR -> when (val l = left.getType(environment) to right.getType(environment)) {
            BOOL to BOOL -> BOOL
            else -> INVALID.typeMismatch(listOf(
                BOOL,
                BOOL
            ), l.toList(), this)
        }

    }

    private fun TernaryExpression.getType(environment: Environment<LanguageType>): LanguageType = when (operator) {
        IF -> {
            val typeCond = condition.getType(environment)
            if (typeCond !is BOOL) {
                INVALID.typeMismatch(
                    listOf(BOOL), listOf(typeCond), this)
            } else {
                // similar code as in BinaryOperator EQ, but not really worth refactor
                val leftType = left.getType(environment)
                val rightType = right.getType(environment)
                if (leftType == rightType) {
                    leftType
                } else {
                    INVALID.typeMismatch(
                        "Expected types to be the same", listOf(rightType, leftType), this)
                }
            }
        }
    }


    fun isCorrect(program: List<ASTNode>): Boolean {
        var toReturn = true

        for (n in program) {
            val t = n.getType(environment)
            if (t is INVALID) {
                println("${t.message}, on node $t")
                toReturn = false
            }
        }

        return toReturn
    }

}


