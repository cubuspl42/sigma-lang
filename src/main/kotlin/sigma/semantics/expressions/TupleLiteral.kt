package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.TupleLiteralTerm
import sigma.evaluation.values.Symbol
import sigma.semantics.types.IllType
import sigma.semantics.types.TupleType
import sigma.syntax.expressions.ExpressionTerm

class TupleLiteral(
    override val term: TupleLiteralTerm,
    val orderedAssociations: List<OrderedAssociation>,
    val unorderedAssociations: Set<UnorderedAssociation>,
) : Expression() {

    data class OrderedAssociation(
        val targetIndex: Int,
        val passedValue: Expression,
    )

    data class UnorderedAssociation(
        val targetName: Symbol,
        val passedValue: Expression,
    )

    data class Entry(
        val name: Symbol,
        val value: Expression,
    ) {
        companion object {
            fun build(
                typeScope: TypeScope,
                declarationScope: DeclarationScope,
                entry: TupleLiteralTerm.UnorderedAssociation,
            ): Entry = Entry(
                name = entry.targetName,
                value = Expression.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    term = entry.passedValue,
                ),
            )
        }
    }

    sealed interface InferredTypeOutcome

    data class InferredTypeResult(
        val type: TupleType,
    ) : InferredTypeOutcome

    data class DuplicatedKeyError(
        val duplicatedKey: PrimitiveValue,
    ) : InferredTypeOutcome, SemanticError

    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: TupleLiteralTerm,
        ): TupleLiteral {
            val orderedAssociations =
                term.associations.filterIsInstance<TupleLiteralTerm.OrderedAssociation>().mapIndexed { index, it ->
                    OrderedAssociation(
                        targetIndex = index,
                        passedValue = Expression.build(
                            typeScope = typeScope,
                            declarationScope = declarationScope,
                            term = it.passedValue,
                        ),
                    )
                }

            val unorderedAssociations =
                term.associations.filterIsInstance<TupleLiteralTerm.UnorderedAssociation>().map {
                    UnorderedAssociation(
                        targetName = it.targetName,
                        passedValue = Expression.build(
                            typeScope = typeScope,
                            declarationScope = declarationScope,
                            term = it.passedValue,
                        ),
                    )
                }.toSet()

            return TupleLiteral(
                term = term,
                orderedAssociations = orderedAssociations,
                unorderedAssociations = unorderedAssociations,
            )
        }
    }

    private val inferredTypeOutcome: Computation<InferredTypeOutcome> = Computation.combine2(
        Computation.traverseList(
            orderedAssociations,
        ) { entry ->
            entry.passedValue.inferredType.thenJust { entry.targetIndex to it }
        },
        Computation.traverseList(
            unorderedAssociations.toList()
        ) { entry ->
            entry.passedValue.inferredType.thenJust { entry.targetName to it }
        },
    ) { orderedEntryPairs, unorderedEntryPairs ->
        val unorderedEntryPairByName = unorderedEntryPairs.groupBy { it.first }

        val firstDuplicatedKey = unorderedEntryPairByName.entries.firstNotNullOfOrNull { (name, entryPairs) ->
            name.takeIf { entryPairs.size > 1 }
        }

        if (firstDuplicatedKey == null) {
            InferredTypeResult(
                type = TupleType(
                    orderedEntries = orderedEntryPairs.mapIndexed { index, (name, type) ->
                        TupleType.OrderedEntry(
                            index = index,
                            name = null,
                            type = type,
                        )
                    },
                    unorderedEntries = unorderedEntryPairs.map { (name, type) ->
                        TupleType.UnorderedEntry(
                            name = name,
                            type = type
                        )
                    }.toSet(),
                ),
            )
        } else {
            DuplicatedKeyError(
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
}

