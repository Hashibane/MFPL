package expression

import environment.IEnvironment

sealed interface IExpression<in U, out T> {
    // property, because its state
    val environment: IEnvironment

    // function, because its behavior
    fun compute(): T
}