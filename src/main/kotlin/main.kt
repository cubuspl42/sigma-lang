import sigma.evaluation.values.EvaluationStackExhaustionError
import sigma.evaluation.values.Value
import sigma.evaluation.values.ValueResult
import sigma.semantics.Project

fun main() {
    val store = Project.ResourceStore(javaClass = object {}.javaClass)
    val loader = Project.Loader.create(store = store)
    val program = loader.load(fileBaseName = "problem")

    when (val evaluationResult = program.evaluateResult()) {
        EvaluationStackExhaustionError -> println("Error: call stack exhausted")
        is ValueResult -> println("Result: ${evaluationResult.value.dump()}")
    }
}
