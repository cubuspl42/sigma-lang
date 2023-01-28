import sigma.compiler.Compiler

const val sourceName = "problem.sigma"

fun main() {
    val source = getResourceAsText(sourceName) ?: throw RuntimeException("Couldn't load the source file")

    val program = Compiler.initialize().load(
        sourceName = sourceName,
        source = source,
    )

    println("Type: ${program.inferType()}")

    println("Result: ${program.evaluate()}")
}
