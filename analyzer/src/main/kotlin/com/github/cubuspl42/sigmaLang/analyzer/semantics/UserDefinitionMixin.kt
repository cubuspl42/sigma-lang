package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.UserDefinition.UnmatchedInferredTypeError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm

class UserDefinitionMixin(
    private val outerScope: StaticScope,
    private val term: DefinitionTerm,
) : UserDefinition {
    private val annotatedTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            Expression.build(
                outerScope = outerScope,
                term = it,
            )
        }
    }

    override val annotatedTypeThunk: Thunk<Type>? by lazy {
        this.annotatedTypeBody?.let { expression ->
            expression.bind(dynamicScope = BuiltinScope).thenJust { it.asType!! }
        }
    }

    override val body: Expression by lazy {
        Expression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.annotatedTypeThunk?.value
        val inferredType = body.inferredType.value

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
        ) + body.errors
    }

    override val name: Symbol
        get() = term.name

    override val effectiveTypeThunk: Thunk<Type> by lazy {
        annotatedTypeThunk ?: body.inferredType
    }
}
