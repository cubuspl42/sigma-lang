package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
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

    private val resolved: ClassifiedIntroduction? by lazy {
        outerScope.resolveName(name = referredName)
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val resolvedIntroduction = resolved

        if (resolvedIntroduction != null) {
            val inferredTargetType = compute(resolvedIntroduction.computedEffectiveType)

            DiagnosedAnalysis(
                analysis = Analysis(
                    inferredType = inferredTargetType,
                    classifiedValue = when(resolvedIntroduction) {
                        is ConstantDefinition -> object : ConstClassificationContext<Value>() {
                            override val valueThunk: Thunk<Value>
                                get() = resolvedIntroduction.valueThunk
                        }

                        is VariableIntroduction -> object : VariableClassificationContext<Value>() {
                            override val referredDeclarations: Set<Introduction>
                                get() = setOf(resolvedIntroduction)

                            override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
                                name = referredName,
                            ) ?: throw RuntimeException(
                                "Unresolved reference at run-time: $referredName",
                            )
                        }
                    }
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
