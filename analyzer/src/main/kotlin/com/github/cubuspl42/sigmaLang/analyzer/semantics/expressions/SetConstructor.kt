package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorTerm

class SetConstructor(
    override val outerScope: StaticScope,
    override val term: SetConstructorTerm,
    val elements: Set<Expression>,
) : Expression() {
    companion object {
        fun build(
            context: BuildContext,
            term: SetConstructorTerm,
        ): SetConstructor = SetConstructor(
            outerScope = context.outerScope,
            term = term,
            elements = term.elements.map {
                Expression.build(
                    context = context,
                    term = it,
                )
            }.toSet(),
        )
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

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.traverseList(elements.toList()) {
            it.classifiedValue
        }.transform { elements ->
            SetValue(elements = elements.toSet())
        }
    }

    override val subExpressions: Set<Expression> = elements

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
