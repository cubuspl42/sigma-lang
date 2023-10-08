package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.VariableIntroduction

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

    private val resolved: Introduction? by lazy {
        outerScope.resolveName(name = referredName)
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        when (val resolvedIntroduction = resolved) {
            is Declaration -> {
                DiagnosedAnalysis(
                    analysis = Analysis(
                        inferredType = resolvedIntroduction.annotatedType,
                    ),
                    directErrors = emptySet(),
                )
            }

            is Definition -> {
                val targetAnalysis = compute(resolvedIntroduction.body.computedAnalysis)

                DiagnosedAnalysis(
                    analysis = targetAnalysis,
                    directErrors = emptySet(),
                )
            }

            null -> DiagnosedAnalysis.fromError(
                UnresolvedNameError(
                    location = term?.location,
                    name = referredName,
                )
            )

            else -> throw UnsupportedOperationException("Unexpected introduction type: $resolvedIntroduction")
        }
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        val resolvedIntroduction =
            this.resolved ?: throw IllegalStateException("Unresolved reference at classification time: $referredName")

        when (resolvedIntroduction) {
            is Declaration -> object : VariableClassificationContext<Value>() {
                override val referredDeclarations: Set<Declaration>
                    get() = setOf(resolvedIntroduction)

                override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(referredName)
                    ?: throw IllegalStateException("Unresolved reference at run-time: $referredName")
            }


            is Definition -> resolvedIntroduction.body.classifiedValue

            else -> throw UnsupportedOperationException("Unexpected introduction type: $resolvedIntroduction")
        }
    }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )
}
