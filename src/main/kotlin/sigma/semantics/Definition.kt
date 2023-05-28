package sigma.semantics

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.DefinitionTerm
import sigma.syntax.SourceLocation

abstract class Definition : Declaration() {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation,
        val matchResult: Type.MatchResult,
    ) : SemanticError

    protected abstract val term: DefinitionTerm

    protected abstract val typeScope: TypeScope

    private val declaredType by lazy {
        term.valueType?.evaluate(
            typeScope = typeScope,
        )
    }

    private val unmatchedInferredTypeError: UnmatchedInferredTypeError? by lazy {
        val declaredType = this.declaredType
        val inferredType = definer.inferredType.value

        if (declaredType != null && inferredType != null) {
            val matchResult = declaredType.match(inferredType)

            if (matchResult.isFull()) null
            else UnmatchedInferredTypeError(
                location = definer.location,
                matchResult = matchResult,
            )
        } else null
    }

    final override val inferredType: Computation<Type> by lazy {
        when (val it = declaredType) {
            null -> definer.inferredType
            else -> Computation.pure(it)
        }
    }

    final override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            unmatchedInferredTypeError
        ) + definer.errors
    }

    abstract val definer: Expression
}