package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm

class OrderedTupleTypeConstructor(
    override val outerScope: StaticScope,
    override val term: OrderedTupleTypeConstructorTerm,
    val elements: List<Element>,
) : TupleTypeConstructor() {
    data class Element(
        val name: Symbol?,
        val type: Expression,
    ) {
        data class Analysis(
            val name: Symbol?,
            val typeAnalysis: Expression.Analysis,
        )
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: OrderedTupleTypeConstructorTerm,
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

    override val computedClassifiedValue: Computation<ClassificationContext<Value>?> = Computation {
        val elementsAnalyses = elements.map {
            val typeAnalysis = compute(it.type.computedAnalysis) ?: return@Computation null

            Element.Analysis(
                name = it.name,
                typeAnalysis = typeAnalysis,
            )
        }

        ClassificationContext.traverseList(elementsAnalyses) { elementAnalysis ->
            elementAnalysis.typeAnalysis.classifiedValue.transform { elementAnalysis.name to it }
        }.transform { elementPairs ->
            OrderedTupleType(
                elements = elementPairs.map { (name, typeValue) ->
                    OrderedTupleType.Element(
                        name = name,
                        type = typeValue.asType!!,
                    )
                },
            ).asValue
        }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements) { element ->
        element.type.bind(dynamicScope = dynamicScope).thenJust { elementType ->
            OrderedTupleType.Element(
                name = element.name, type = elementType.asType!!,
            )
        }
    }.thenJust { elements ->
        OrderedTupleType(elements = elements).asValue
    }

    override val subExpressions: Set<Expression> = elements.map { it.type }.toSet()
}
