package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDefinition.UnmatchedInferredTypeError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression.Companion.Analysis
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.QuasiExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm

class UserDefinitionMixin(
    private val outerScope: StaticScope,
    private val term: DefinitionTerm,
) : AssignmentDefinition {
    private val annotatedTypeBody: TypeExpression? by lazy {
        term.declaredTypeBody?.let {
            TypeExpression.build(
                outerScope = outerScope,
                term = it,
            )
        }
    }

    override val annotatedType by lazy { annotatedTypeBody?.typeOrIllType }

    override val assignedBody: Expression by lazy {
        Expression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.annotatedType
        val bodyAnalysis = assignedBody.computedAnalysis.getOrCompute()
        val inferredType = bodyAnalysis?.inferredType

        if (declaredType != null && inferredType != null) {
            val matchResult = declaredType.match(inferredType)

            if (matchResult.isFull()) null
            else UnmatchedInferredTypeError(
                location = assignedBody.location,
                matchResult = matchResult,
            )
        } else null
    }

    override val errors: Set<SemanticError> by lazy {
        val annotatedTypeErrors = annotatedTypeBody?.errors ?: emptySet()

        setOfNotNull(
            unmatchedInferredTypeError
        ) + annotatedTypeErrors + assignedBody.directErrors
    }

    override val body by lazy {
        object : QuasiExpression() {
            override val computedAnalysis = when (val type = annotatedType) {
                null -> assignedBody.computedAnalysis.transform { bodyAnalysisOrNull ->
                    bodyAnalysisOrNull?.let { bodyAnalysis ->
                        Analysis(
                            inferredType = bodyAnalysis.inferredType,
                        )
                    }
                }

                else -> Expression.Computation.pure(
                    Analysis(inferredType = type),
                )
            }

            override val classifiedValue: ClassificationContext<Value>
                get() = assignedBody.classifiedValue
        }
    }

    override val name: Symbol
        get() = term.name
}
