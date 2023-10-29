package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ParametricType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TypeSpecificationTerm

class TypeSpecification(
    override val term: TypeSpecificationTerm,
    private val subjectLazy: Lazy<Expression>,
    private val metaArgumentThunk: Thunk<DictValue>,
) : FirstOrderExpression() {
    companion object {
        fun build(
            context: BuildContext,
            term: TypeSpecificationTerm,
        ): Lazy<TypeSpecification> {
            val subjectLazy = Expression.build(
                context = context,
                term = term.subject,
            ).asLazy()

            val metaArgumentConstructor by TypeExpression.build(
                outerScope = context.outerScope,
                term = term.argument,
            ).asLazy()

            val metaArgumentThunk = Thunk.lazy {
                metaArgumentConstructor.constClassified!!.valueThunk.thenJust { it as DictValue }
            }

            return lazy {
                TypeSpecification(
                    term = term,
                    subjectLazy = subjectLazy,
                    metaArgumentThunk = metaArgumentThunk,
                )
            }
        }
    }

    data class NonGenericCallError(
        override val location: SourceLocation?,
        val illegalSubjectType: TypeAlike,
    ) : SemanticError

    val subject: Expression by subjectLazy

    val metaArgument: DictValue by lazy { metaArgumentThunk.value!! }

    override val outerScope: StaticScope
        get() = StaticScope.Empty

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectType = compute(subject.inferredTypeOrIllType) ?: return@buildDiagnosedAnalysisComputation null

        if (subjectType !is ParametricType) {
            return@buildDiagnosedAnalysisComputation DiagnosedAnalysis.fromError(
                NonGenericCallError(
                    location = term.location,
                    illegalSubjectType = subjectType,
                )
            )
        }

        return@buildDiagnosedAnalysisComputation DiagnosedAnalysis(
            inferredType = subjectType.parametrize(metaArgument),
        )
    }

    override val subExpressions: Set<Expression>
        get() = setOf(subject)

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = subject.bindDirectly(
        dynamicScope = dynamicScope
    )
}
