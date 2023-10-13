package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.evaluateValueHacky
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.TranslationDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.ArrayTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.DictTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*
import com.github.cubuspl42.sigmaLang.analyzer.utils.SetUtils

abstract class Expression {
    abstract class Computation<out R> {
        data class Context(
            private val visitedExpressions: Set<Expression>,
        ) {
            fun withAnalyzed(
                expression: Expression,
            ): Context = Context(
                visitedExpressions = visitedExpressions + expression,
            )

            fun isAnalyzed(
                expression: Expression,
            ): Boolean = visitedExpressions.contains(expression)

            fun <R> compute(
                computation: Computation<R>,
            ): R = computation.compute(
                context = this,
            )

            companion object {
                val Empty: Expression.Computation.Context = Context(
                    visitedExpressions = emptySet(),
                )
            }
        }

        data class Result<R>(
            val result: R,
        )


        companion object {
            fun <R> pure(result: R): Computation<R> = object : Computation<R>() {
                override fun computeDirectly(context: Context): R = result
            }
        }

        fun <R2> transform(
            function: (R) -> R2,
        ): Computation<R2> = object : Computation<R2>() {
            override fun computeDirectly(
                context: Context,
            ): R2 = function(
                this@Computation.compute(context = context),
            )
        }

        abstract fun computeDirectly(context: Context): R

        fun compute(context: Context): R = when (val cachedResult = this.cachedResult) {
            null -> {
                val newResult = computeDirectly(context = context)

                this.cachedResult = Result(result = newResult)

                newResult
            }

            else -> cachedResult.result
        }

        private var cachedResult: Result<R>? = null

        fun getOrCompute(): R = compute(
            context = Context.Empty,
        )
    }

    fun <R> Computation(
        block: Computation.Context.() -> R,
    ): Computation<R> = object : Computation<R>() {
        override fun computeDirectly(
            context: Context,
        ): R = context.block()
    }

    interface Stub<out T> {
        companion object {
            fun <T> of(
                value: T,
            ): Stub<T> = object : Stub<T> {
                override val resolved: T = value
            }
        }

        val resolved: T
    }

    fun buildDiagnosedAnalysisComputation(
        block: Computation.Context.() -> DiagnosedAnalysis?,
    ): Computation<DiagnosedAnalysis?> = Computation {
        if (isAnalyzed(this@Expression)) {
            DiagnosedAnalysis(
                analysis = null,
                // TODO: A specific error?
                directErrors = emptySet(),
            )
        } else {
            this.withAnalyzed(this@Expression).block()
        }
    }

    abstract class Analysis {
        abstract val inferredType: MembershipType
    }

    fun Analysis(
        inferredType: MembershipType,
    ): Analysis = object : Analysis() {
        override val inferredType: MembershipType = inferredType
    }

    data class DiagnosedAnalysis(
        val analysis: Analysis?,
        val directErrors: Set<SemanticError>,
    ) {
        companion object {
            fun fromError(
                error: SemanticError,
            ): DiagnosedAnalysis = DiagnosedAnalysis(
                analysis = null,
                directErrors = setOf(error),
            )
        }
    }

    data class BuildContext(
        val outerMetaScope: StaticScope,
        val outerScope: StaticScope,
    ) {
        companion object {
            val Empty = Expression.BuildContext(
                outerMetaScope = StaticScope.Empty,
                outerScope = StaticScope.Empty,
            )

            val Builtin = Expression.BuildContext(
                outerMetaScope = BuiltinScope,
                outerScope = BuiltinScope,
            )
        }
    }

    companion object {
        fun build(
            context: BuildContext,
            term: ExpressionTerm,
        ): Expression.Stub<Expression> = when (term) {
            is AbstractionConstructorTerm -> AbstractionConstructor.build(
                context = context,
                term = term,
            )

            is ArrayTypeConstructorTerm -> ArrayTypeConstructor.build(
                context = context,
                term = term,
            )

            is CallTerm -> Call.build(
                context = context,
                term = term,
            )

            is FieldReadTerm -> FieldRead.build(
                context = context,
                term = term,
            )

            is IntLiteralTerm -> IntLiteral.build(
                context = context,
                term = term,
            )


            is StringLiteralTerm -> StringLiteral.build(
                context = context,
                term = term,
            )

            is IsUndefinedCheckTerm -> IsUndefinedCheck.build(
                context = context,
                term = term,
            )

            is LetExpressionTerm -> LetExpression.build(
                context = context,
                term = term,
            )

            is ReferenceTerm -> Reference.build(
                context = context,
                term = term,
            )

            is TupleConstructorTerm -> TupleConstructor.build(
                context = context,
                term = term,
            )

            is DictConstructorTerm -> DictConstructor.build(
                context = context,
                term = term,
            )

            is SetConstructorTerm -> SetConstructor.build(
                context = context,
                term = term,
            )

            is IfExpressionTerm -> IfExpression.build(
                context = context,
                term = term,
            )

            is TupleTypeConstructorTerm -> TupleTypeConstructor.build(
                context = context,
                term = term,
            )

            is DictTypeConstructorTerm -> DictTypeConstructor.build(
                context = context,
                term = term,
            )

            is FunctionTypeConstructorTerm -> FunctionTypeConstructor.build(
                context = context,
                term = term,
            )

            is UnionTypeConstructorTerm -> UnionTypeConstructor.build(
                context = context,
                term = term,
            )

            is ParenTerm -> TODO()
        }

        fun parse(
            source: String,
        ): Expression {
            val term = ExpressionSourceTerm.parse(source = source)

            return Expression.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = StaticScope.Empty,
                ),
                term = term,
            ).resolved
        }
    }

    val location: SourceLocation?
        get() = term?.location

    // TODO: Probably nuke
    abstract val outerScope: StaticScope

    val directErrors: Set<SemanticError> by lazy {
        computedDiagnosedAnalysis.getOrCompute()?.directErrors ?: emptySet()
    }

    val errors: Set<SemanticError> by lazy {
        directErrors + SetUtils.unionAllOf(subExpressions) { it.directErrors }
    }

    protected abstract val term: ExpressionTerm?

    protected abstract val computedDiagnosedAnalysis: Expression.Computation<DiagnosedAnalysis?>

    abstract val classifiedValue: ClassificationContext<Value>

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

    // TODO: Nuke
    abstract fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value>

    // TODO: Refactor!
    fun analyzeAsType(
        outerScope: StaticScope,
    ): TypeExpression.DiagnosedAnalysis {
        val valueThunk by lazy {
            bind(
                dynamicScope = TranslationDynamicScope(
                    staticScope = outerScope,
                ),
            )
        }

        return when (val outcome = valueThunk.outcome) {
            is EvaluationError -> TypeExpression.DiagnosedAnalysis(
                type = null,
                errors = setOf(
                    TypeExpression.TypeEvaluationError(
                        evaluationError = outcome,
                    ),
                ),
            )

            is EvaluationResult -> {
                val value = outcome.value

                when (val type = value.asType) {
                    null -> TypeExpression.DiagnosedAnalysis(
                        type = null,
                        errors = setOf(
                            TypeExpression.NonTypeValueError(
                                value = value,
                            ),
                        ),
                    )

                    else -> TypeExpression.DiagnosedAnalysis(
                        type = type,
                        errors = emptySet(),
                    )
                }
            }
        }
    }
}
