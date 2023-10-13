package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTerm

abstract class OrderedTupleConstructor : TupleConstructor() {
    abstract override val term: OrderedTupleConstructorTerm

    abstract val elements: List<Expression>

    companion object {
        fun build(
            context: BuildContext,
            term: OrderedTupleConstructorTerm,
        ): Stub<OrderedTupleConstructor> = object : Stub<OrderedTupleConstructor> {
            override val resolved: OrderedTupleConstructor by lazy {
                object : OrderedTupleConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: OrderedTupleConstructorTerm = term

                    override val elements: List<Expression> by lazy {
                        term.elements.map {
                            Expression.build(
                                context = context,
                                term = it,
                            ).resolved
                        }
                    }
                }
            }
        }
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
            ),
            directErrors = emptySet(),
        )
    }



    override val subExpressions: Set<Expression>
        get() = elements.toSet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements) {
        it.bind(dynamicScope = dynamicScope)
    }.thenJust { elements ->
        ArrayTable(elements = elements)
    }
}
