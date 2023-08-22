package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm

class OrderedTupleTypeConstructor(
    override val outerScope: StaticScope,
    override val term: OrderedTupleTypeConstructorSourceTerm,
    val elements: List<Element>,
) : TupleTypeConstructor() {
    data class Element(
        val name: Symbol?,
        val type: Expression,
    )

    companion object {
        fun build(
            outerScope: StaticScope,
            term: OrderedTupleTypeConstructorSourceTerm,
        ): OrderedTupleTypeConstructor = OrderedTupleTypeConstructor(
            outerScope = outerScope,
            term = term,
            elements = term.elements.map {
                Element(
                    name = it.name,
                    type = Expression.build(
                        outerScope = outerScope,
                        term = it.type,
                    ),
                )
            },
        )
    }

    override val inferredType: Thunk<OrderedTupleType>
        get() = TODO()

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.traverseList(elements) { element ->
        element.type.bind(scope = scope).thenJust { elementType ->
            OrderedTupleType.Element(
                name = element.name, type = elementType as Type,
            )
        }
    }.thenJust { elements ->
        OrderedTupleType(elements = elements)
    }

    override val errors: Set<SemanticError>
        get() = TODO()
}
