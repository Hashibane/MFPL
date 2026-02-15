package compiling.tokenizer

class Tokenizer(override val keywords: List<String>) : ITokenizer {
    //override val keywords;
    override fun tokenize(text: String): List<Statement> {
        TODO("Not yet implemented")
    }

    private fun tokenizeLine(line: String): Statement {
        val t = Keyword.entries.toTypedArray().map( {})
        return listOf("")
    }
}