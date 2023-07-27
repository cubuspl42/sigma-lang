package sigma.semantics

import sigma.evaluation.scope.Scope
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation

abstract class ValueDefinition : ValueDeclaration {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation,
        val matchResult: Type.MatchResult,
    ) : SemanticError

    protected abstract val declarationScope: StaticScope

    private val declaredType: Type? by lazy {
        definedTypeBody?.evaluate(scope = BuiltinScope) as? Type
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.declaredType
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

    final override val effectiveValueType: Computation<Type> by lazy {
        when (val it = declaredType) {
            null -> body.inferredType
            else -> Computation.pure(it)
        }
    }

    val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            unmatchedInferredTypeError
        ) + body.errors
    }

    abstract val definedTypeBody: Expression?

    abstract val body: Expression
}
