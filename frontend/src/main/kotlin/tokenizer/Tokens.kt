package compiling.tokenizer

sealed interface Token {
    val representation: String

    companion object TokenFactory {
        fun parseToken(text: String): Token? {
            return LanguageToken.lookupTable[text] ?:
                when {
                    text.contains("^[a-zA-Z_](\\p{Alnum}|_)*$".toRegex()) -> IDENTIFIER(text)
                    text.contains("^[0-9]+$".toRegex()) -> INTEGER(text)
                    text.contains("^[0-9]+\\.[0-9]*$".toRegex()) -> DOUBLE(text)
                    text.contains("^\".*\"$".toRegex()) -> STRING(text)
                    else -> null
                }
        }
    }
}
/*
        An identifier is valid when:
        - It contains only alphanumeric characters or _ sign
        - The first char is not a digit
 */
data class IDENTIFIER(override val representation: String) : Token

/*
        An integer is valid when:
        - It contains only digits
 */
data class INTEGER(override val representation: String) : Token

/*
        A double is valid when:
        - Exactly one dot
        - One or more digits on the left of the dot
        - Zero or more digits on the right of the dot
*/
data class DOUBLE(override val representation: String) : Token

/*
        A string is valid when:
        - It begins and ends with a quote mark - "
        - Contains exactly two quote marks
*/
data class STRING(override val representation: String) : Token

// types which have small domain (like bool and unit) can be tokenized here
enum class LanguageToken(override val representation: String) : Token {

    // keywords
    VAL("val"),
    FUN("fun"),

    // special operators
    IF("if"),
    ELSE("else"),
    NEG("~"),
    ASSIGN("="),

    // arithmetic operators
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),

    // comparison operators
    EQ("=="),
    NEQ("!="),
    LT("<"),
    GT(">"),
    LTE("<="),
    GTE(">="),

    // logic operators
    OR("||"),
    AND("&&"),

    // literal parsing
    COMA(","),
    LPAR("("),
    RPAR(")"),
    LCUR("{"),
    RCUR("}"),
    UNIT("UNIT"),
    TRUE("true"),
    FALSE("false"),

    // types
    BOOL("Bool"),
    INT("Int"),
    DOUBLE("Double"),
    STRING("String"),
    TUPLE("Tuple");

    companion object {
        val splitTokens = LanguageToken.entries.map { it.representation }
        val lookupTable = LanguageToken.entries.associateBy { it.representation }
    }

}