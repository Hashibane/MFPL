package compiling.tokenizer

private val stringSplitTokens = listOf(" ", "\t")

class Tokenizer {
    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()

        val lines = input.split("\n")

        outer@ for ((lineIndex, line) in lines.withIndex()) {
            val tokensStrings = line.split(*(LanguageToken.splitTokens + stringSplitTokens).toTypedArray())
            tokensStrings.forEach {
                val token = Token.parseToken(it) ?: break@outer
                tokens.add(token)
            }
        }

        return tokens
    }
}