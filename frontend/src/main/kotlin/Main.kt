import compiling.tokenizer.tokenize


fun main() {
    val program = """
        val a = 14 + 4 + { 3 + 4 }()
        val b = "hello"
        
        if (~a == 21) {
            val a = "bebe"
        } else b
        
        fun test(a: Int, b: Int) -> Bool {
            fun test2() {
                megatest
            }
            a + b 
        }
    """.trimIndent()

    println(tokenize(program))
}

