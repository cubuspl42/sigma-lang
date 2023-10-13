package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorTerm

abstract class SetConstructor : Expression() {
    abstract override val term: SetConstructorTerm

    abstract val elements: Set<Expression>

    companion object {
        fun build(
            context: BuildContext,
            term: SetConstructorTerm,
        ): Stub<SetConstructor> = object : Stub<SetConstructor> {
            override val resolved: SetConstructor by lazy {
                object : SetConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: SetConstructorTerm = term

                    override val elements: Set<Expression> by lazy {
                        term.elements.map {
                            Expression.build(
                                context = context,
                                term = it,
                            ).resolved
                        }.toSet()
                    }
                }
            }
        }
    }

    data class InconsistentElementTypeError(
        override val location: SourceLocation,
    ) : SemanticError

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val elementsAnalyses = elements.map {
            compute(it.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        }

        val distinctiveElementTypes = elementsAnalyses.map { it.inferredType }.toSet()

        val elementType = distinctiveElementTypes.singleOrNull()

        if (elementType != null) {
            DiagnosedAnalysis(
                analysis = Analysis(
                    inferredType = SetType(
                        elementType = elementType,
                    ),
                ),
                directErrors = emptySet(),
            )
        } else {
            DiagnosedAnalysis.fromError(
                InconsistentElementTypeError(
                    location = term.location,
                )
            )
        }
    }



    override val subExpressions: Set<Expression>
        get() = elements

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements.toList()) {
        it.bind(dynamicScope = dynamicScope)
    }.thenJust { elements ->
        SetValue(
            elements = elements.toSet(),
        )
    }
}
