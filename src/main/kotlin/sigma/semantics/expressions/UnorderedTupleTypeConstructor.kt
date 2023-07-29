package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateInitialValue
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.DictType
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

    override val inferredType: Thunk<Type>
        get() = TODO()

    override fun bind(scope: Scope): Thunk<Value> = UnorderedTupleType(
        valueTypeByName = entries.associate {
            it.name to it.value.bind(
                scope = scope,
            ).evaluateInitialValue() as Type
        }
    ).asThunk

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}
