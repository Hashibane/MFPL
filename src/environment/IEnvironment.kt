package environment

interface IEnvironment {
    fun create(arg: Any?)
    fun doesExist(variableName: String): Boolean

    fun <T> get(variableName: String): T
    fun set(variableName: String, value: Any?)
}