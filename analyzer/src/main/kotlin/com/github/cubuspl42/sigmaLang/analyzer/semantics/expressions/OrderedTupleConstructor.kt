package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
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

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val elementsAnalyses = elements.map {
            compute(it.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        }

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = OrderedTupleType(
                    elements = elementsAnalyses.map {
                        OrderedTupleType.Element(
                            name = null,
                            type = it.inferredType,
                        )
                    },
                ),
                classifiedValue = ClassificationContext.traverseList(
                    elementsAnalyses
                ) { it.classifiedValue }.transform { elements ->
                    ArrayTable(elements = elements)
                },
            ),
            directErrors = emptySet(),
        )
    }

    override val subExpressions: Set<Expression> = elements.toSet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements) {
        it.bind(dynamicScope = dynamicScope)
    }.thenJust { elements ->
        ArrayTable(elements = elements)
    }
}
