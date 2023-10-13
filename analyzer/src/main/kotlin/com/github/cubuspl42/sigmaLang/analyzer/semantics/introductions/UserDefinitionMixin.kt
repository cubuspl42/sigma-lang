package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDefinition.UnmatchedInferredTypeError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm


class UserDefinitionMixin(
    private val context: Expression.BuildContext,
    private val term: DefinitionTerm,
) : EmbodiedUserDefinition {
    private val annotatedTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            TypeExpression.build(
                outerMetaScope = context.outerMetaScope,
                term = it,
            ).resolved
        }
    }

    override val annotatedType by lazy {
        annotatedTypeBody?.analyzeAsType(outerScope = context.outerScope)?.typeOrIllType
    }

    override val body: Expression by lazy {
        Expression.build(
            context = context,
            term = term.body,
        ).resolved
    }

    override val bodyStub: Expression.Stub<Expression> = object : Expression.Stub<Expression> {
        override val resolved: Expression
            get() = body
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.annotatedType
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
        val annotatedTypeErrors = annotatedTypeBody?.errors ?: emptySet()

        setOfNotNull(
            unmatchedInferredTypeError
        ) + annotatedTypeErrors + body.directErrors
    }

    override val name: Identifier
        get() = term.name

    override val computedEffectiveType: Expression.Computation<MembershipType> by lazy {
        annotatedType?.let { Expression.Computation.pure(it) } ?: body.inferredTypeOrIllType
    }
}
