package expression

sealed interface LanguageType {
    data object INT : LanguageType
    data object DOUBLE : LanguageType
    data object STRING : LanguageType
    data object TUPLE : LanguageType
    data object UNIT : LanguageType
    data object BOOL : LanguageType
    data class FUNCTION(val arguments: List<LanguageType>, val returnType: LanguageType) : LanguageType

    data class INVALID(val message: String, val node: ASTNode) : LanguageType {
        companion object {
            fun unknownError(node: ASTNode): LanguageType = INVALID("An unknown error occurred", node)

            fun variableNotExist(variableName: String, node: ASTNode): LanguageType =
                INVALID("Variable \"$variableName\" does not exist", node)

            fun typeMismatch(prefix: String, types: List<LanguageType>, node: ASTNode): LanguageType =
                INVALID("$prefix, received types ${types.joinToString()}", node)

            fun typeMismatch(expectedTypes: List<LanguageType>, receivedTypes: List<LanguageType>, node: ASTNode): LanguageType =
                INVALID("Expected types: " +
                        "${expectedTypes.joinToString()}, received ${receivedTypes.joinToString()}", node)

            fun invalidArgumentCount(expectedNumber: Int, receivedNumber: Int, node: ASTNode): LanguageType =
                INVALID("Expected $expectedNumber arguments, received $receivedNumber", node)

        }
    }
}
