package expression

import environment.IEnvironment

class Variable<T>(val variableName: String) : IExpression<T, T> {
    override fun compute(): T =
        if (memoryManager.doesExist(this.variableName))
            memoryManager.get(this.variableName)
        else
            throw Exception()
}