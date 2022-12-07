import sigma.Program

const val sourceName = "problem.sigma"

fun main() {
    val source = getResourceAsText(sourceName) ?: throw RuntimeException("Couldn't load the source file")

    val result = Program.evaluate(
        sourceName = sourceName,
        source = source,
    )

    println(result)
}
