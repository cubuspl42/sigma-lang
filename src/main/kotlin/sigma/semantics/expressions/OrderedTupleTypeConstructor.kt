package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.evaluateInitialValue
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm

class OrderedTupleTypeConstructor(
    override val term: OrderedTupleTypeConstructorTerm,
    val elements: List<Element>,
) : TupleTypeConstructor() {
    data class Element(
        val name: Symbol?,
        val type: Expression,
    )

    companion object {
        fun build(
            declarationScope: StaticScope,
            term: OrderedTupleTypeConstructorTerm,
        ): OrderedTupleTypeConstructor = OrderedTupleTypeConstructor(
            term = term,
            elements = term.elements.map {
                Element(
                    name = it.name,
                    type = Expression.build(
                        declarationScope = declarationScope,
                        term = it.type,
                    ),
                )
            },
        )
    }

    override val inferredType: Thunk<OrderedTupleType>
        get() = TODO()

    override fun bind(scope: Scope): Thunk<Value> {
        return OrderedTupleType(
            elements = elements.map {
                OrderedTupleType.Element(
                    name = it.name, type = it.type.bind(
                        scope = scope,
                    ).evaluateInitialValue() as Type
                )
            },
        ).asThunk
    }

    override val errors: Set<SemanticError>
        get() = TODO()
}
