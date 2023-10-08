package com.github.cubuspl42.sigmaLang.analyzer

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResourceProjectStore

fun main() {
    val projectStore = ResourceProjectStore(javaClass = object {}.javaClass)
    val projectLoader = Project.Loader.create()
    val project = projectLoader.load(
        projectStore = projectStore,
        mainModuleName = "problem",
    )

    when (val outcome = project.entryPoint.valueThunk.evaluateInitial()) {
        is EvaluationError -> println("Error: $outcome")
        is EvaluationResult -> println("Result: ${outcome.value.dump()}")
    }
}
