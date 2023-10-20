package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType

abstract class TypeConstructor : Expression() {
    final override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = TypeType,
            ),
            directErrors = emptySet(),
        )
    }
}
