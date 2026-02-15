package compiling.tokenizer

enum class Keyword(
    val representation: String
) {
    FUNCTION("fun"),
    DECLARATION("dec"),
    ASSIGN("="),
    EQUALS("=="),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    LPAR("("),
    RPAR(")")
}