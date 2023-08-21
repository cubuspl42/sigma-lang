import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project

fun main() {
    val store = Project.ResourceStore(javaClass = object {}.javaClass)
    val loader = Project.Loader.create(store = store)
    val program = loader.load(fileBaseName = "problem")

    when (val outcome = program.evaluateResult()) {
        is EvaluationError -> println("Error: $outcome")
        is EvaluationResult -> println("Result: ${outcome.value.dump()}")
    }
}
