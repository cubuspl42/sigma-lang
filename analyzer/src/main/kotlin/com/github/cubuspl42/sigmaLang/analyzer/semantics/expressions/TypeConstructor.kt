package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType

abstract class TypeConstructor : Expression() {
    final override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val classifiedValue = compute(computedClassifiedValue) ?: return@buildDiagnosedAnalysisComputation null

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = TypeType,
                classifiedValue = classifiedValue,
            ),
            directErrors = emptySet(),
        )
    }

    abstract val computedClassifiedValue: Expression.Computation<ClassificationContext<Value>?>
}
