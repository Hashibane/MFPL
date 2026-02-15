package compiling.tokenizer

typealias Token = String
typealias Statement = List<Token>

interface ITokenizer {
    val keywords: List<String>
    fun tokenize(text: String): List<Statement>
}