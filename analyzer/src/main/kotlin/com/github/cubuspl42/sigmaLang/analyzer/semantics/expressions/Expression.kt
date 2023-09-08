package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.evaluateValueHacky
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.CallTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ParenTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.StringLiteralTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm

abstract class Expression {
    sealed class AnalyzedExpression {
        abstract val errors: Set<SemanticError>
    }

    abstract class UnacceptableExpression : AnalyzedExpression() {
        final override val errors: Set<SemanticError>
            get() = criticalErrors

        abstract val criticalErrors: Set<SemanticError>
    }

    object TransitiveUnacceptableExpression : UnacceptableExpression() {
        override val criticalErrors: Set<SemanticError> = emptySet()
    }

    abstract class AcceptableExpression : AnalyzedExpression() {
        final override val errors: Set<SemanticError>
            get() = nonCriticalErrors

        open val nonCriticalErrors: Set<SemanticError> = emptySet()

        abstract val inferredType: Type

        abstract val classifiedExpression: ClassifiedExpression?
    }


    sealed class ClassifiedExpression {
        fun wrapOf(transform: (Value) -> Value): ClassifiedExpression = when (this) {
            is ConstExpression -> object : ConstExpression() {
                override val valueThunk: Thunk<Value> = this@ClassifiedExpression.valueThunk.thenJust(transform)
            }

            is VariableExpression -> object : VariableExpression() {
                override val referredDeclarations: Set<Declaration>
                    get() = this@ClassifiedExpression.referredDeclarations

                override fun bind(dynamicScope: DynamicScope): Thunk<Value> =
                    this@ClassifiedExpression.bind(dynamicScope = dynamicScope).thenJust(transform)
            }
        }
    }

    abstract class ConstExpression : ClassifiedExpression() {
        abstract val valueThunk: Thunk<Value>
    }

    abstract class VariableExpression : ClassifiedExpression() {
        abstract val referredDeclarations: Set<Declaration>

        // Thought: Map in the form Declaration -> Value
        abstract fun bind(
            dynamicScope: DynamicScope,
        ): Thunk<Value>
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: ExpressionTerm,
        ): Expression = when (term) {
            is AbstractionTerm -> Abstraction.build(
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

    protected abstract val term: ExpressionTerm?

    // TODO: Migrate everything to analyzedExpression if it makes sense; now analyzedExpression/inferredType/errors
    //       implementations are circular
    open val analyzedExpression: Thunk<AnalyzedExpression> by lazy {
        inferredType.thenJust { type ->
            when (type) {
                // TODO: Nuke IllType after migartion?
                is IllType -> object : UnacceptableExpression() {
                    override val criticalErrors: Set<SemanticError>
                        get() = this@Expression.errors
                }

                else -> object : AcceptableExpression() {
                    override val nonCriticalErrors: Set<SemanticError>
                        get() = this@Expression.errors

                    override val inferredType: Type = type

                    override val classifiedExpression: ClassifiedExpression? = null
                }
            }
        }
    }

    open val inferredType: Thunk<Type> by lazy {
        analyzedExpression.thenJust {
            when (it) {
                is AcceptableExpression -> it.inferredType
                is UnacceptableExpression -> IllType
            }
        }
    }

    open val errors: Set<SemanticError> by lazy {
        analyzedExpression.value?.errors ?: emptySet()
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

    fun bindTranslated(
        staticScope: StaticScope,
    ): Thunk<Value> = bind(
        dynamicScope = TranslationDynamicScope(
            staticScope = staticScope,
        ),
    )
}
