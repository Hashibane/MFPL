package environment
/*
import compiling.exceptions.VariableException
import expression.expression.IExpression

class Environment(val parent: Environment?) {
    private val environment: MutableMap<String, IExpression> = mutableMapOf()

    fun create(variableName: String, value: IExpression) {
        environment[variableName] = value
    }

    fun get(variableName: String): IExpression =
        environment[variableName] ?:
        parent?.get(variableName) ?:
        throw VariableException("Variable $variableName does not exist")


    fun set(variableName: String, value: IExpression) {
        if (variableName in environment) {
            environment[variableName] = value
        } else {
            parent?.set(variableName, value) ?:
            throw VariableException("Variable $variableName does not exist")
        }
    }


}
*/
