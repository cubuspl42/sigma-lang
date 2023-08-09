package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.ArrayTable
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.OrderedTupleConstructorTerm

class OrderedTupleConstructor(
    override val term: OrderedTupleConstructorTerm,
    val elements: List<Expression>,
) : TupleConstructor() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: OrderedTupleConstructorTerm,
        ): OrderedTupleConstructor = OrderedTupleConstructor(
            term = term,
            elements = term.elements.map {
                Expression.build(
                    declarationScope = declarationScope,
                    term = it,
                )
            },
        )
    }

    override val inferredType: Thunk<OrderedTupleType> = Thunk.traverseList(
        elements,
    ) { element ->
        element.inferredType.thenJust { elementType ->
            OrderedTupleType.Element(
                name = null,
                type = elementType,
            )
        }
    }.thenJust { elements ->
        OrderedTupleType(
            elements = elements,
        )
    }

    override val errors: Set<SemanticError> by lazy {
        elements.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.traverseList(elements) {
        it.bind(scope = scope)
    }.thenJust { elements ->
        ArrayTable(elements = elements)
    }
}
