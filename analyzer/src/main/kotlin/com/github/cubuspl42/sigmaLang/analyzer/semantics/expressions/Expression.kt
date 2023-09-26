package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.evaluateValueHacky
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*
import com.github.cubuspl42.sigmaLang.analyzer.utils.SetUtils

abstract class Expression {
    abstract class Computation<out R> {
        class Context(
            private val state: State,
        ) {
            fun <R> compute(
                computation: Computation<R>,
            ): R = computation.compute(
                state = state,
            )
        }

        data class State(
            val visitedExpressions: Set<Expression>,
        ) {
            fun withVisited(
                expression: Expression,
            ): State = State(
                visitedExpressions = visitedExpressions + expression,
            )

            companion object {
                val Empty: Expression.Computation.State = State(
                    visitedExpressions = emptySet(),
                )
            }
        }

        data class Result<R>(
            val result: R,
        )


        companion object {
            fun <R> pure(result: R): Computation<R> = object : Computation<R>() {
                override fun computeDirectly(state: State): R = result
            }
        }

        fun <R2> transform(
            function: (R) -> R2,
        ): Computation<R2> = object : Computation<R2>() {
            override fun computeDirectly(
                state: State,
            ): R2 = function(
                this@Computation.compute(state = state),
            )
        }

        abstract fun computeDirectly(state: State): R

        fun compute(state: State): R = when (val cachedResult = this.cachedResult) {
            null -> {
                val newResult = computeDirectly(state = state)

                this.cachedResult = Result(result = newResult)

                newResult
            }

            else -> cachedResult.result
        }

        private var cachedResult: Result<R>? = null

        fun getOrCompute(): R = compute(
            state = State.Empty,
        )
    }

    private fun <R> Computation(
        handleCycle: () -> R,
        block: Computation.Context.() -> R,
    ): Computation<R> = object : Computation<R>() {
        override fun computeDirectly(
            state: State,
        ): R = if (state.visitedExpressions.contains(this@Expression)) {
            handleCycle()
        } else Context(
            state = state.withVisited(this@Expression),
//            state = state,
        ).block()
    }

    fun buildDiagnosedAnalysisComputation(
        block: Computation.Context.() -> DiagnosedAnalysis?,
    ): Computation<DiagnosedAnalysis?> = Computation(
        handleCycle = {
            DiagnosedAnalysis(
                analysis = null,
                directErrors = emptySet(),
            )
        },
        block = block,
    )

    data class Analysis(
        val inferredType: MembershipType,
    )

    data class DiagnosedAnalysis(
        val analysis: Analysis?,
        val directErrors: Set<SemanticError>,
    ) {
        companion object {
            fun fromInferredType(
                inferredType: MembershipType,
            ): DiagnosedAnalysis = DiagnosedAnalysis(
                analysis = Analysis(
                    inferredType = inferredType,
                ),
                directErrors = emptySet(),
            )

            fun fromError(
                error: SemanticError,
            ): DiagnosedAnalysis = DiagnosedAnalysis(
                analysis = null,
                directErrors = setOf(error),
            )
        }
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: ExpressionTerm,
        ): Expression = when (term) {
            is AbstractionTerm -> AbstractionConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is CallTerm -> Call.build(
                outerScope = outerScope,
                term = term,
            )

            is FieldReadTerm -> FieldRead.build(
                outerScope = outerScope,
                term = term,
            )

            is IntLiteralTerm -> IntLiteral.build(
                outerScope = outerScope,
                term = term,
            )


            is StringLiteralTerm -> StringLiteral.build(
                outerScope = outerScope,
                term = term,
            )

            is IsUndefinedCheckTerm -> IsUndefinedCheck.build(
                outerScope = outerScope,
                term = term,
            )

            is LetExpressionTerm -> LetExpression.build(
                outerScope = outerScope,
                term = term,
            )

            is ReferenceTerm -> Reference.build(
                outerScope = outerScope,
                term = term,
            )

            is TupleConstructorTerm -> TupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is DictConstructorTerm -> DictConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is SetConstructorTerm -> SetConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is IfExpressionTerm -> IfExpression.build(
                outerScope = outerScope,
                term = term,
            )

            is TupleTypeConstructorTerm -> TupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is ArrayTypeConstructorTerm -> ArrayTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is DictTypeConstructorTerm -> DictTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is FunctionTypeConstructorTerm -> FunctionTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is UnionTypeConstructorTerm -> UnionTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is GenericTypeConstructorTerm -> TODO()

            is ParenTerm -> TODO()
        }

        fun parse(
            source: String,
        ): Expression {
            val term = ExpressionSourceTerm.parse(source = source)

            return Expression.build(
                outerScope = StaticScope.Empty,
                term = term,
            )
        }
    }

    val location: SourceLocation?
        get() = term?.location

    abstract val outerScope: StaticScope

    val directErrors: Set<SemanticError> by lazy {
        computedDiagnosedAnalysis.getOrCompute()?.directErrors ?: emptySet()
    }

    val errors: Set<SemanticError> by lazy {
        directErrors + SetUtils.unionAllOf(subExpressions) { it.directErrors }
    }

    protected abstract val term: ExpressionTerm?

    protected abstract val computedDiagnosedAnalysis: Expression.Computation<DiagnosedAnalysis?>

    val computedAnalysis: Expression.Computation<Analysis?> by lazy {
        computedDiagnosedAnalysis.transform {
            it?.analysis
        }
    }

    private val inferredTypeOrNull: Expression.Computation<MembershipType?> by lazy {
        computedAnalysis.transform { it?.inferredType }
    }

    val inferredTypeOrIllType: Computation<MembershipType> by lazy {
        inferredTypeOrNull.transform { it ?: IllType }
    }

    abstract val subExpressions: Set<Expression>

    val expressionMap: ExpressionMap by lazy {
        val selfMap = term?.let {
            ExpressionMap(
                map = mapOf(it to this),
            )
        } ?: ExpressionMap.Empty

        selfMap.unionWith(
            subExpressions.fold(
                initial = ExpressionMap.Empty,
            ) { acc, subExpression ->
                acc.unionWith(subExpression.expressionMap)
            },
        )
    }

    fun evaluateValue(
        context: EvaluationContext,
        dynamicScope: DynamicScope,
    ): Value? = bind(dynamicScope = dynamicScope).evaluateValueHacky(context = context)

    abstract fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value>
}
