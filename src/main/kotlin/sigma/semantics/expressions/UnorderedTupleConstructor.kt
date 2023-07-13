package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.semantics.Computation
import sigma.semantics.TypeScope
import sigma.evaluation.values.PrimitiveValue
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.values.DictValue
import sigma.semantics.types.IllType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation

class UnorderedTupleConstructor(
    override val term: UnorderedTupleConstructorTerm,
    val entries: Set<Entry>,
) : TupleConstructor() {
    data class Entry(
        val name: Symbol,
        val value: Expression,
    ) {
        companion object {
            fun build(
                typeScope: TypeScope,
                declarationScope: DeclarationScope,
                entry: UnorderedTupleConstructorTerm.Entry,
            ): Entry = Entry(
                name = entry.name,
                value = Expression.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    term = entry.value,
                ),
            )
        }
    }

    sealed interface InferredTypeOutcome

    data class InferredTypeResult(
        val type: UnorderedTupleType,
    ) : InferredTypeOutcome

    data class DuplicatedKeyError(
        override val location: SourceLocation,
        val duplicatedKey: PrimitiveValue,
    ) : InferredTypeOutcome, SemanticError

    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: UnorderedTupleConstructorTerm,
        ): UnorderedTupleConstructor = UnorderedTupleConstructor(
            term = term,
            entries = term.entries.map {
                Entry.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    entry = it,
                )
            }.toSet(),
        )
    }

    private val inferredTypeOutcome: Computation<InferredTypeOutcome> = Computation.traverseList(
        entries.toList()
    ) { entry ->
        entry.value.inferredType.thenJust { entry.name to it }
    }.thenJust { entryPairs ->
        val entryPairByName = entryPairs.groupBy { it.first }

        val firstDuplicatedKey =
            entryPairByName.entries.firstNotNullOfOrNull { (name, entryPairs) -> name.takeIf { entryPairs.size > 1 } }

        if (firstDuplicatedKey == null) {
            InferredTypeResult(
                type = UnorderedTupleType(
                    valueTypeByName = entryPairs.toMap()
                ),
            )
        } else {
            DuplicatedKeyError(
                location = term.location,
                duplicatedKey = firstDuplicatedKey,
            )
        }

    }

    override val inferredType: Computation<Type> = inferredTypeOutcome.thenJust {
        when (it) {
            is InferredTypeResult -> it.type
            is DuplicatedKeyError -> IllType
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredTypeOutcome.value as? DuplicatedKeyError,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = DictValue(
        entries = entries.associate {
            it.name to it.value.evaluate(scope = scope)
        },
    )
}
