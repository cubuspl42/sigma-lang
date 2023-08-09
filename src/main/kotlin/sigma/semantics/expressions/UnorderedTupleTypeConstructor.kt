package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.MetaType
import sigma.semantics.types.Type
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm

class UnorderedTupleTypeConstructor(
    override val term: UnorderedTupleTypeConstructorTerm,
    val entries: Set<UnorderedTupleConstructor.Entry>,
) : TupleTypeConstructor() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: UnorderedTupleTypeConstructorTerm,
        ): UnorderedTupleTypeConstructor = UnorderedTupleTypeConstructor(
            term = term,
            entries = term.entries.map {
                UnorderedTupleConstructor.Entry.build(
                    declarationScope = declarationScope,
                    entry = it,
                )
            }.toSet(),
        )
    }

    override val inferredType: Thunk<Type> = MetaType.asThunk

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.traverseList(entries.toList()) { entry ->
        entry.value.bind(
            scope = scope,
        ).thenJust { entryType ->
            entry.name to (entryType as Type)
        }

    }.thenJust { entries ->
        UnorderedTupleType(
            valueTypeByName = entries.toMap(),
        )
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}
