package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType

abstract class TypeConstructor : FirstOrderExpression() {
    final override val computedAnalysis = buildAnalysisComputation {
        Analysis(
            typeInference = TypeInference(
                inferredType = TypeType,
            ),
            directErrors = emptySet(),
        )
    }
}
