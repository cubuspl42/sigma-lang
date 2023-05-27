package sigma.semantics

import sigma.Computation
import sigma.TypeScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.DefinitionTerm

abstract class Definition : Declaration() {
    data class UnmatchedInferredTypeError(
        val declaredType: Type,
        val inferredType: Type,
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

        if (declaredType != null && inferredType != null && declaredType != inferredType) {
            UnmatchedInferredTypeError(
                declaredType = declaredType,
                inferredType = inferredType,
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
