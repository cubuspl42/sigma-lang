package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.VariableIntroduction

abstract class Reference : Expression() {
    abstract override val term: ReferenceTerm?

    abstract val referredName: Identifier

    data class UnresolvedNameError(
        override val location: SourceLocation?,
        val name: Identifier,
    ) : SemanticError

    data class NonValueDeclarationError(
        override val location: SourceLocation?,
        val name: Identifier,
    ) : SemanticError

    companion object {
        fun build(
            context: BuildContext,
            term: ReferenceTerm,
        ): Stub<Reference> = object : Stub<Reference> {
            override val resolved: Reference by lazy {
                object : Reference() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: ReferenceTerm = term

                    override val referredName: Identifier = term.referredName
                }
            }
        }
    }

    private val resolvedIntroduction: ClassifiedIntroduction? by lazy {
        outerScope.resolveName(name = referredName)
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val resolvedIntroduction = resolvedIntroduction

        if (resolvedIntroduction != null) {
            val inferredTargetType = compute(resolvedIntroduction.computedEffectiveType)

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

    override val classifiedValue: ClassificationContext<Value> by lazy {
        val resolvedIntroduction = this.resolvedIntroduction
            ?: throw IllegalStateException("Unresolved reference at classification time: $referredName")

        when (resolvedIntroduction) {
            is ConstantDefinition -> object : ConstClassificationContext<Value>() {
                override val valueThunk: Thunk<Value>
                    get() = resolvedIntroduction.valueThunk
            }

            is VariableIntroduction -> object : VariableClassificationContext<Value>() {
                override val referredDeclarations: Set<Introduction>
                    get() = setOf(resolvedIntroduction)

                override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
                    name = referredName,
                ) ?: throw IllegalStateException(
                    "Unresolved dynamic reference at run-time: $referredName",
                )
            }
        }
    }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )
}
