

class Fibonacci(val value: Int, val previous: () -> Fibonacci) {
    companion object {
        fun first(): Fibonacci = Fibonacci(1) { first() }

    }

    val next: Fibonacci
        get() = Fibonacci(this.previous().value + this.value) { this }
}

fun main() {

    var current = Fibonacci.first()
    for (i in 1..10) {
        println("liczba $i: ${current.value}")
        current = current.next
    }
}

