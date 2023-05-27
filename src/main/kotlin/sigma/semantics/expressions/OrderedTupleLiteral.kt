package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.OrderedTupleLiteralTerm

class OrderedTupleLiteral(
    override val term: OrderedTupleLiteralTerm,
    val elements: List<Expression>,
) : TupleLiteral() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: OrderedTupleLiteralTerm,
        ): OrderedTupleLiteral = OrderedTupleLiteral(
            term = term,
            elements = term.elements.map {
                Expression.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    term = it,
                )
            },
        )
    }

    override val inferredType: Computation<OrderedTupleType> = Computation.traverseList(
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
}

