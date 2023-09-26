package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDefinition.UnmatchedInferredTypeError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm

class UserDefinitionMixin(
    private val outerScope: StaticScope,
    private val term: DefinitionTerm,
) : EmbodiedUserDefinition {
    private val annotatedTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            Expression.build(
                outerScope = outerScope,
                term = it,
            )
        }
    }

    override val annotatedTypeThunk: Thunk<MembershipType>? by lazy {
        this.annotatedTypeBody?.let { expression ->
            expression.bindTranslated(staticScope = outerScope).thenJust { it.asType!! }
        }
    }

    val annotatedType by lazy { annotatedTypeThunk?.let { it.value ?: IllType } }

    override val body: Expression by lazy {
        Expression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.annotatedTypeThunk?.value
        val bodyAnalysis = body.computedAnalysis.getOrCompute()
        val inferredType = bodyAnalysis?.inferredType

        if (declaredType != null && inferredType != null) {
            val matchResult = declaredType.match(inferredType)

            if (matchResult.isFull()) null
            else UnmatchedInferredTypeError(
                location = body.location,
                matchResult = matchResult,
            )
        } else null
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            unmatchedInferredTypeError
        ) + body.directErrors
    }

    override val name: Symbol
        get() = term.name

    override val computedEffectiveType: Expression.Computation<MembershipType> by lazy {
        annotatedType?.let { Expression.Computation.pure(it) } ?: body.inferredTypeOrIllType
    }
}
