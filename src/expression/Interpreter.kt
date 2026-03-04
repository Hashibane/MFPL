package expression

import environment.Environment
import environment.get
import environment.set
import expression.BinaryExpression.BinaryOperator.*
import expression.TernaryExpression.TernaryOperator.*
import expression.UnaryExpression.UnaryOperator.*
import expression.RuntimeObject.Companion.createPrimitiveObject

class Interpreter {
    private val environment = Environment<RuntimeObject>()

    fun ASTNode.getValue(environment: Environment<RuntimeObject>): RuntimeObject {
        print("$this")
        return when (this) {
            is BinaryExpression ->
                createPrimitiveObject(
                    when (operator) {
                        PLUS -> left.getValue(environment) + right.getValue(environment)
                        MINUS -> left.getValue(environment) - right.getValue(environment)
                        MULTIPLY -> left.getValue(environment) * right.getValue(environment)
                        DIVIDE -> left.getValue(environment) / right.getValue(environment)
                        EQ -> left.getValue(environment) == right.getValue(environment)
                        NEQ -> left.getValue(environment) == right.getValue(environment)
                        LT -> left.getValue(environment) < right.getValue(environment)
                        GT -> left.getValue(environment) > right.getValue(environment)
                        LTE -> left.getValue(environment) <= right.getValue(environment)
                        GTE -> left.getValue(environment) >= right.getValue(environment)
                        AND -> left.getValue(environment) and right.getValue(environment)
                        OR -> right.getValue(environment) or right.getValue(environment)
                    }
                )

            is BoolConstant -> BoolObject(this.value)
            is DoubleConstant -> DoubleObject(this.value)
            is FunctionCall -> {
                fun resolveFunctionCall(function: FunctionObject, parameters: List<RuntimeObject>): RuntimeObject {
                    val argDiff = function.argsNeeded.size - parameters.size
                    if (argDiff < 0) {
                        return ExceptionObject("$function was called with more arguments than defined. Args: $parameters")
                    }

                    with (function.closure) {
                        function.argsNeeded.zip(parameters).forEach { this[it.first] = it.second }

                        return if (argDiff > 0)
                            FunctionObject(function.node, Environment(this), function.argsNeeded.drop(parameters.size))
                        else
                            function.node.getValue(this)
                    }
                }

                when (definition) {
                    is VariableReference -> {
                        when (val function = environment[definition.name]) {
                            null -> ExceptionObject("Variable ${definition.name} does not exist")
                            is FunctionObject -> resolveFunctionCall(
                                function,
                                parameters.map { it.getValue(environment) }
                            )
                            else -> ExceptionObject("$definition is not a callable object")
                        }
                    }
                    is FunctionDefinition -> resolveFunctionCall(
                        definition.getValue(environment), parameters.map { it.getValue(environment) }
                    )
                    else -> ExceptionObject("$definition is not a callable object")
                }
            }
            is FunctionDefinition -> getValue(environment)

            is FunctionNode -> {
                val valueBuffer = current.getValue(environment)
                next?.getValue(environment) ?: valueBuffer
            }

            is IntegerConstant -> IntegerObject(this.value)
            is StringConstant -> StringObject(this.value)
            is TernaryExpression ->
                when (operator) {
                    IF ->
                        when (val valueObject = condition.getValue(environment)) {
                            is BoolObject ->
                                if (valueObject.value)
                                    left.getValue(environment)
                                else
                                    right.getValue(environment)
                            else -> ExceptionObject("$condition is not a boolean object")
                        }
                }

            is TupleConstant -> TupleObject(this.value.map { it.getValue(environment) })
            is UnaryExpression -> when (operator) {
                NOT -> -node.getValue(environment)
            }
            UnitConstant -> UnitObject
            is VariableAssignment -> {
                environment[name] = value.getValue(environment)
                UnitObject
            }
            is VariableReference -> environment[name] ?: ExceptionObject("Variable $name does not exist")
        }
    }

    private fun FunctionDefinition.getValue(environment: Environment<RuntimeObject>): FunctionObject =
        FunctionObject(node, Environment(environment), arguments.map { it.first })

    fun runProgram(program: List<ASTNode>) {
        for (node in program) {
            print(node.getValue(environment))
        }
    }
}