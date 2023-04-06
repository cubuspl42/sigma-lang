import sigma.semantics.Project

fun main() {
    val store = Project.ResourceStore(javaClass = object {}.javaClass)
    val loader = Project.Loader.create(store = store)
    val program = loader.load(fileBaseName = "problem")

    println("Type: ${program.inferResultType().dump()}")

    println("Result: ${program.evaluateResult().dump()}")
}
