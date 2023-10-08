package com.github.cubuspl42.sigmaLang.applications.euler

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationStackExhaustionError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResourceProjectStore

fun solveProblem(n: Int): EvaluationOutcome<Value> {
    val store = ResourceProjectStore(javaClass = object {}.javaClass)
    val loader = Project.Loader.create()
    val project = loader.load(
        projectStore = store,
        mainModuleName = "problem$n",
    )

    val errors = project.errors

    println()
    println("[Problem $n]")
    println()

    if (errors.isNotEmpty()) {
        println("Semantic errors:")
        errors.forEach {
            println(it.dump())
        }
    }

    val evaluationResult = project.entryPoint.valueThunk.evaluateInitial()

    when (evaluationResult) {
        EvaluationStackExhaustionError -> println("Error: call stack exhausted!")
        is EvaluationResult -> println("Result: ${evaluationResult.value.dump()}")
    }

    println()

    return evaluationResult
}
