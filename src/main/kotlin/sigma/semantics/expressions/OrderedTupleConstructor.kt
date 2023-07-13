package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Value
import sigma.evaluation.values.ArrayTable
import sigma.semantics.Computation
import sigma.semantics.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.OrderedTupleConstructorTerm

class OrderedTupleConstructor(
    override val term: OrderedTupleConstructorTerm,
    val elements: List<Expression>,
) : TupleConstructor() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: OrderedTupleConstructorTerm,
        ): OrderedTupleConstructor = OrderedTupleConstructor(
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

    override fun evaluate(
        scope: Scope,
    ): Value = ArrayTable(
        elements = elements.map {
            it.evaluate(scope = scope)
        },
    )
}

