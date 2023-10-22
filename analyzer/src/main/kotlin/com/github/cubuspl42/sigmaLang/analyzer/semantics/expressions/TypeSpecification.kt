package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.BinaryOperationPrototype
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.*
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

class TypeSpecification(
    override val term: TypeSpecificationTerm,
    private val subjectLazy: Lazy<Expression>,
    private val metaArgumentLazy: Lazy<TupleType>,
) : FirstOrderExpression() {
    companion object {
        fun build(
            context: BuildContext,
            term: TypeSpecificationTerm,
        ): Lazy<TypeSpecification> {
            TODO()
//            return lazy {
//                TypeSpecification(
//                    term = term,
//
//                )
//            }
        }
    }

    data class NonGenericCallError(
        override val location: SourceLocation?,
        val illegalSubjectType: TypeAlike,
    ) : SemanticError

    val subject: Expression by subjectLazy

    val metaArgument: TupleType by metaArgumentLazy
    override val outerScope: StaticScope
        get() = StaticScope.Empty

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectType = compute(subject.inferredTypeOrIllType) ?: return@buildDiagnosedAnalysisComputation null

        if (subjectType !is GenericType) {
            return@buildDiagnosedAnalysisComputation DiagnosedAnalysis.fromError(
                NonGenericCallError(
                    location = term.location,
                    illegalSubjectType = subjectType,
                )
            )
        }

        TODO()
    }

    override val subExpressions: Set<Expression>
        get() = setOf(subject)

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = subject.bindDirectly(
        dynamicScope = dynamicScope
    )
}
