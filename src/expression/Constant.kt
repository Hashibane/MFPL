package expression

import environment.IEnvironment

class Constant<T>(val value: T, override val environment: IEnvironment) : IExpression<T, T> {
    override fun compute(): T = this.value
}
