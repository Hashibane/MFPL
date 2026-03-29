package compiling.parser

sealed interface Token {
    val representation: String
}

data class IDENTIFIER(override val representation: String) : Token

data class INTEGER(override val representation: String) : Token
data class DOUBLE(override val representation: String) : Token
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

        fun tokenize(word: String) = lookupTable[word]
    }
}