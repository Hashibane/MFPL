package compiling.tokenizer

private val languageTokens = LanguageToken.splitTokens
private val stringSplitTokens = listOf(' ', '\t')
private val consumedTokens = listOf(':')

data class DebugInfo(val line: Int, val column: Int)

fun tokenize(input: String): List<Pair<Token, DebugInfo>> {
    val errors = mutableListOf<String>()

    val tokens = mutableListOf<Pair<Token, DebugInfo>>()

    val lines = input.split("\n")
    val wordBuffer = StringBuilder()

    for ((lineNum, line) in lines.withIndex()) {
        for ((column, char) in line.withIndex()) {
            val candidate = Token.parseToken(wordBuffer.toString())

            when (char) {
                in consumedTokens  -> continue
                in stringSplitTokens if wordBuffer.isEmpty() -> continue
                in stringSplitTokens if wordBuffer.isNotEmpty() -> {
                    if (candidate == null) {
                        errors.add("Unable to parse element $wordBuffer at line $lineNum, column $column")
                    } else {
                        tokens.add(candidate to DebugInfo(lineNum, column))
                    }

                    wordBuffer.clear()
                }
                else -> {
                    // TODO - fix ~a
                    if (candidate != null && char.toString() in languageTokens) {
                        tokens.add(candidate to DebugInfo(lineNum, column - 1))
                        wordBuffer.clear()
                    }
                    wordBuffer.append(char)
                }

            }
        }
    }

    print(errors)

    return tokens
}