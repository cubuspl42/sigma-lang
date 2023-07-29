package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.DictType
import sigma.semantics.types.IllType
import sigma.semantics.types.PrimitiveType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.DictConstructorTerm

class DictConstructor(
    override val term: DictConstructorTerm,
    val associations: List<Association>,
) : Expression() {
    class Association(
        val key: Expression,
        val value: Expression,
    ) {
        companion object {
            fun build(
                declarationScope: StaticScope,
                term: DictConstructorTerm.Association,
            ): Association = Association(
                key = Expression.build(
                    declarationScope = declarationScope,
                    term = term.key,
                ),
                value = Expression.build(
                    declarationScope = declarationScope,
                    term = term.value,
                ),
            )
        }
    }

    companion object {
        fun build(
            declarationScope: StaticScope,
            term: DictConstructorTerm,
        ): DictConstructor = DictConstructor(
            term = term,
            associations = term.associations.map {
                Association.build(
                    declarationScope = declarationScope,
                    term = it,
                )
            },
        )
    }

    sealed interface InferredKeyTypeOutcome

    data class InferredKeyTypeResult(
        val keyType: PrimitiveType,
    ) : InferredKeyTypeOutcome

    sealed interface InferredKeyTypeError : InferredKeyTypeOutcome, SemanticError

    data class InconsistentKeyTypeError(
        override val location: SourceLocation,
    ) : InferredKeyTypeError

    data class NonPrimitiveKeyTypeError(
        override val location: SourceLocation,
        val keyType: Type,
    ) : InferredKeyTypeError, SemanticError

    sealed interface InferredValueTypeOutcome

    data class InferredValueTypeResult(
        val valueType: Type,
    ) : InferredValueTypeOutcome

    data class InconsistentValueTypeError(
        override val location: SourceLocation,
    ) : InferredValueTypeOutcome, SemanticError

    private val inferredKeyTypeOutcome: Computation<InferredKeyTypeOutcome> = Computation.traverseList(
        associations
    ) {
        it.key.inferredType
    }.thenJust { keyTypes ->
        val distinctiveKeyTypes = keyTypes.toSet()

        val keyType = distinctiveKeyTypes.singleOrNull()

        if (keyType != null) {
            val primitiveKeyType = keyType as? PrimitiveType

            if (primitiveKeyType != null) {
                InferredKeyTypeResult(
                    keyType = keyType,
                )
            } else {
                NonPrimitiveKeyTypeError(
                    location = term.location,
                    keyType = keyType,
                )
            }
        } else {
            InconsistentKeyTypeError(
                location = term.location,
            )
        }
    }

    private val inferredValueTypeOutcome: Computation<InferredValueTypeOutcome> = Computation.traverseList(
        associations
    ) {
        it.value.inferredType
    }.thenJust { valueTypes ->
        val distinctiveValueTypes = valueTypes.toSet()

        val valueType = distinctiveValueTypes.singleOrNull()

        if (valueType != null) {
            InferredValueTypeResult(
                valueType = valueType,
            )
        } else {
            InconsistentValueTypeError(
                location = term.location,
            )
        }
    }

    override val inferredType: Computation<Type> = Computation.combine2(
        inferredKeyTypeOutcome,
        inferredValueTypeOutcome,
    ) { inferredKeyTypeOutcome, inferredValueTypeOutcome ->
        if (inferredKeyTypeOutcome is InferredKeyTypeResult && inferredValueTypeOutcome is InferredValueTypeResult) {
            DictType(
                keyType = inferredKeyTypeOutcome.keyType,
                valueType = inferredValueTypeOutcome.valueType,
            )
        } else {
            IllType
        }
    }

    override fun bind(scope: Scope): Thunk<*> {
        return DictValue(
            entries = associations.associate {
                val key = it.key.bind(
                    scope = scope,
                ).evaluateInitialValue() as PrimitiveValue

                val value = it.value.bind(
                    scope = scope,
                ).evaluateInitialValue()

                key to value
            },
        ).asThunk
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredKeyTypeOutcome.value as? InferredKeyTypeError,
            inferredValueTypeOutcome.value as? InconsistentValueTypeError,
        )
    }
}
