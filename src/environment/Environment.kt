package environment

import kotlin.collections.contains
import kotlin.collections.set

data class Environment<T>(val parent: Environment<T>? = null, val environment: MutableMap<String, T> = mutableMapOf())

operator fun <T> Environment<T>?.contains(variableName: String): Boolean =
    if (this == null) false
    else parent.contains(variableName)

operator fun <T> Environment<T>?.set(variableName: String, value: T) {
    when (this) {
        null -> return
        else if variableName in environment -> environment[variableName] = value
        else -> environment[variableName] = value
    }
}

operator fun <T> Environment<T>.get(variableName: String): T? =
    environment[variableName] ?: parent?.get(variableName)