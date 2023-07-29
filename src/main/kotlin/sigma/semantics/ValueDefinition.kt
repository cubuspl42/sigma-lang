package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.semantics.expressions.EvaluationContext
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation

abstract class ValueDefinition : ValueDeclaration {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation,
        val matchResult: Type.MatchResult,
    ) : SemanticError

    protected abstract val declarationScope: StaticScope

    private val declaredType: Thunk<Type>? by lazy {
        declaredTypeBody?.let { expression ->
            expression.bind(scope = BuiltinScope).thenJust { it as Type }
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
