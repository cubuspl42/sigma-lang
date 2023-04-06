import sigma.semantics.Project

fun main() {
    val store = Project.ResourceStore(javaClass = object {}.javaClass)
    val loader = Project.Loader.create(store = store)
    val program = loader.load(fileBaseName = "problem")

    println("Result: ${program.evaluateResult().dump()}")
}
