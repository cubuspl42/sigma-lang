package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

abstract class UserDefinition : ValueDeclaration {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: Type.MatchResult,
    ) : SemanticError

    protected abstract val outerScope: StaticScope

    private val declaredType: Thunk<Type>? by lazy {
        declaredTypeBody?.let { expression ->
            expression.bind(dynamicScope = BuiltinScope).thenJust { it.asType!! }
        }
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.declaredType?.value
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

    final override val effectiveValueType: Thunk<Type> by lazy {
        declaredType ?: body.inferredType
    }

    val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            unmatchedInferredTypeError
        ) + body.errors
    }

    abstract val declaredTypeBody: Expression?

    abstract val body: Expression
}
