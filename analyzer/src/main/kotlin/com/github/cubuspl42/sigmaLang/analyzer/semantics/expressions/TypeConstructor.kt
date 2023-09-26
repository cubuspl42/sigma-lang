package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MetaType

abstract class TypeConstructor : Expression() {
    final override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = MetaType,
            ),
            directErrors = emptySet(),
        )
    }
}
