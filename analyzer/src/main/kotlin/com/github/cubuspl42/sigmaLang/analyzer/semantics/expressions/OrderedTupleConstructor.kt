package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorSourceTerm

class OrderedTupleConstructor(
    override val term: OrderedTupleConstructorSourceTerm,
    val elements: List<Expression>,
) : TupleConstructor() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: OrderedTupleConstructorSourceTerm,
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
