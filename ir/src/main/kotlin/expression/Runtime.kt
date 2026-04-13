package expression

import environment.Environment

//TODO
/*
Make adding INTs to DOUBLEs imposible and introduce casting nodes
 */

sealed interface RuntimeObject: Comparable<RuntimeObject> {
    private fun unsupportedOperator(operator: String, other: RuntimeObject? = null): ExceptionObject =
        ExceptionObject("Operation plus not implemented on objects: $this   ${other ?: ""}")

    fun runtimeTypeException(other: RuntimeObject, operation: String): Nothing =
        throw RuntimeException("Types $this and $other are not compatible with $operation")



    operator fun plus(other: RuntimeObject): RuntimeObject = unsupportedOperator("+", other)
    operator fun minus(other: RuntimeObject): RuntimeObject = unsupportedOperator("-", other)
    operator fun times(other: RuntimeObject): RuntimeObject = unsupportedOperator("*", other)
    operator fun div(other: RuntimeObject): RuntimeObject = unsupportedOperator("/", other)

    infix fun or(other: RuntimeObject): RuntimeObject = unsupportedOperator("|", other)
    infix fun and(other: RuntimeObject): RuntimeObject = unsupportedOperator("&", other)

    operator fun unaryMinus(): RuntimeObject = unsupportedOperator("-", this)

    override fun compareTo(other: RuntimeObject): Int = runtimeTypeException(other, "==")

    companion object {
        private fun runtimeCreationException(value: Any?): Nothing {
            throw RuntimeException("Cannot instantiate any primitive type from $value")
        }

        fun createPrimitiveObject(value: Any?): RuntimeObject =
            when (value) {
                null -> UnitObject
                is Boolean -> BoolObject(value)
                is Int -> IntegerObject(value)
                is Double -> DoubleObject(value)
                is String -> StringObject(value)
                is RuntimeObject -> value
                else -> runtimeCreationException(value)
            }
    }
}


inline fun <reified T : RuntimeObject> RuntimeObject.operatorDefinition(operator: String, other: RuntimeObject, body: T.() -> RuntimeObject): RuntimeObject =
    if (other is T) {
        other.body()
    } else {
        runtimeTypeException(other, operator)
    }

inline fun <reified T : RuntimeObject> RuntimeObject.compareDefinition(other: RuntimeObject, body: T.() -> Int): Int =
    if (other is T) {
        other.body()
    } else {
        runtimeTypeException(other, "==")
    }


data object UnitObject: RuntimeObject

data class StringObject(val value: String): RuntimeObject {
    override fun plus(other: RuntimeObject): RuntimeObject = operatorDefinition<StringObject>("+", other) {
        StringObject(this@StringObject.value + value)
    }

    override fun compareTo(other: RuntimeObject): Int = compareDefinition<StringObject>(other) {
        this@StringObject.value.compareTo(value)
    }
}

data class BoolObject(val value: Boolean): RuntimeObject {
    override fun compareTo(other: RuntimeObject): Int = compareDefinition<BoolObject>(other) {
        this@BoolObject.value.compareTo(value)
    }

    override infix fun or(other: RuntimeObject): RuntimeObject = operatorDefinition<BoolObject>("|", other) {
        BoolObject(this@BoolObject.value || value)
    }

    override infix fun and(other: RuntimeObject): RuntimeObject = operatorDefinition<BoolObject>("&", other) {
        BoolObject(this@BoolObject.value && value)
    }

    override operator fun unaryMinus(): RuntimeObject = BoolObject(!value)
}


data class IntegerObject(val value: Int): RuntimeObject {
    override fun compareTo(other: RuntimeObject): Int = compareDefinition<IntegerObject>(other) {
        this@IntegerObject.value.compareTo(value)
    }

    override fun plus(other: RuntimeObject): RuntimeObject = operatorDefinition<IntegerObject>("+", other) {
        IntegerObject(this@IntegerObject.value + value)
    }

    override fun minus(other: RuntimeObject): RuntimeObject = operatorDefinition<IntegerObject>("-", other) {
        IntegerObject(this@IntegerObject.value - value)
    }

    override fun times(other: RuntimeObject): RuntimeObject = operatorDefinition<IntegerObject>("*", other) {
        IntegerObject(this@IntegerObject.value * value)
    }

    override fun div(other: RuntimeObject): RuntimeObject = operatorDefinition<IntegerObject>("/", other) {
        IntegerObject(this@IntegerObject.value / value)
    }

    override operator fun unaryMinus(): RuntimeObject = IntegerObject(-value)
}

data class DoubleObject(val value: Double): RuntimeObject {
    override fun compareTo(other: RuntimeObject): Int = compareDefinition<DoubleObject>(other) {
        this@DoubleObject.value.compareTo(value)
    }

    override fun plus(other: RuntimeObject): RuntimeObject = operatorDefinition<DoubleObject>("+", other) {
        DoubleObject(this@DoubleObject.value + value)
    }

    override fun minus(other: RuntimeObject): RuntimeObject = operatorDefinition<DoubleObject>("-", other) {
        DoubleObject(this@DoubleObject.value - value)
    }

    override fun times(other: RuntimeObject): RuntimeObject = operatorDefinition<DoubleObject>("*", other) {
        DoubleObject(this@DoubleObject.value * value)
    }

    override fun div(other: RuntimeObject): RuntimeObject = operatorDefinition<DoubleObject>("/", other) {
        DoubleObject(this@DoubleObject.value / value)
    }

    override operator fun unaryMinus(): RuntimeObject = DoubleObject(-value)
}
data class TupleObject(val values: List<RuntimeObject>): RuntimeObject {
    //TODO
    //implement tuple operations
}
data class FunctionObject(val node: ASTNode, val closure: Environment<RuntimeObject>, val argsNeeded: List<String>): RuntimeObject
data class ExceptionObject(val message: String): RuntimeObject
