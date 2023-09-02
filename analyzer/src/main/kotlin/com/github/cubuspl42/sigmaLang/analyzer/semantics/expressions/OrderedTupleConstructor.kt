package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTerm

class OrderedTupleConstructor(
    override val outerScope: StaticScope,
    override val term: OrderedTupleConstructorTerm,
    val elements: List<Expression>,
) : TupleConstructor() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: OrderedTupleConstructorTerm,
        ): OrderedTupleConstructor = OrderedTupleConstructor(
            outerScope = outerScope,
            term = term,
            elements = term.elements.map {
                Expression.build(
                    outerScope = outerScope,
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

    override val subExpressions: Set<Expression> = elements.toSet()

    override val errors: Set<SemanticError> by lazy {
        elements.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements) {
        it.bind(dynamicScope = dynamicScope)
    }.thenJust { elements ->
        ArrayTable(elements = elements)
    }
}
