package compiling.tokenizer

private val stringSplitTokens = listOf(" ", "\t")
private val consumedTokens = listOf(":")

fun tokenize(input: String): List<Token> {
    val tokens = mutableListOf<Token>()

    val lines = input.split("\n")

    val splitPattern = (LanguageToken.splitTokens + stringSplitTokens).joinToString("|") { Regex.escape(it) }
    val regex = Regex("(?=$splitPattern)")
    outer@ for ((lineIndex, line) in lines.withIndex()) {
        val tokensStrings = line.split(regex)
        tokensStrings.forEach {
            val rawToken = it.trim()
            if (rawToken.isNotBlank() && rawToken !in consumedTokens) {
                val token = Token.parseToken(rawToken.trim()) ?: break@outer
                tokens.add(token)
            }
        }
    }

    return tokens
}