package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction

class Reference(
    override val outerScope: StaticScope,
    override val term: ReferenceTerm?,
    val referredName: Symbol,
) : Expression() {
    data class UnresolvedNameError(
        override val location: SourceLocation?,
        val name: Symbol,
    ) : SemanticError

    data class NonValueDeclarationError(
        override val location: SourceLocation?,
        val name: Symbol,
    ) : SemanticError

    companion object {
        fun build(
            outerScope: StaticScope,
            term: ReferenceTerm,
        ): Reference = Reference(
            outerScope = outerScope,
            term = term,
            referredName = term.referredName,
        )
    }

    private val resolved: ClassifiedIntroduction? by lazy {
        outerScope.resolveName(name = referredName)
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val resolved = resolved

        if (resolved != null) {
            val inferredTargetType = compute(resolved.computedEffectiveType)

            DiagnosedAnalysis(
                analysis = Analysis(
                    inferredType = inferredTargetType,
                ),
                directErrors = emptySet(),
            )
        } else {
            DiagnosedAnalysis.fromError(
                UnresolvedNameError(
                    location = term?.location,
                    name = referredName,
                )
            )
        }
    }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )
}
