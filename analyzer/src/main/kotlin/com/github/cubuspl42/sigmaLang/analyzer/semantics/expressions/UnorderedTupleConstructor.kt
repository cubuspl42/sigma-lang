package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm

class UnorderedTupleConstructor(
    override val term: UnorderedTupleConstructorSourceTerm,
    val entries: Set<Entry>,
) : TupleConstructor() {
    data class Entry(
        val name: Symbol,
        val value: Expression,
    ) {
        companion object {
            fun build(
                declarationScope: StaticScope,
                entry: UnorderedTupleConstructorSourceTerm.Entry,
            ): Entry = Entry(
                name = entry.name,
                value = Expression.build(
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
            declarationScope: StaticScope,
            term: UnorderedTupleConstructorSourceTerm,
        ): UnorderedTupleConstructor = UnorderedTupleConstructor(
            term = term,
            entries = term.entries.map {
                Entry.build(
                    declarationScope = declarationScope,
                    entry = it,
                )
            }.toSet(),
        )
    }

    private val inferredTypeOutcome: Thunk<InferredTypeOutcome> = Thunk.traverseList(
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

    override val inferredType: Thunk<Type> = inferredTypeOutcome.thenJust {
        when (it) {
            is InferredTypeResult -> it.type
            is DuplicatedKeyError -> IllType
        }
    }

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.traverseList(entries.toList()) { entry ->
        entry.value.bind(scope = scope).thenJust { entryValue ->
            entry.name to entryValue
        }
    }.thenJust { entries ->
        DictValue(
            entries.toMap(),
        )
    }

    override val errors: Set<SemanticError> by lazy {
        val entriesErrors: Set<SemanticError> = entries.fold(emptySet()) { acc, it -> acc + it.value.errors }

        entriesErrors + setOfNotNull(
            inferredTypeOutcome.value as? DuplicatedKeyError,
        )
    }
}
