package compiling.exceptions

class VariableException(message: String): NoSuchElementException(message)

class InstantiationException(message: String): IllegalArgumentException(message)