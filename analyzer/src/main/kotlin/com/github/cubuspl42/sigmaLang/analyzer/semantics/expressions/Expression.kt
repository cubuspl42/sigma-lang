package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.evaluateValueHacky
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

abstract class Expression {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ExpressionSourceTerm,
        ): Expression = when (term) {
            is AbstractionSourceTerm -> Abstraction.build(
                outerScope = outerScope,
                term = term,
            )

            is PostfixCallSourceTerm -> Call.build(
                outerScope = outerScope,
                term = term,
            )

            is InfixCallSourceTerm -> Call.build(
                outerScope = outerScope,
                term = term,
            )


            is FieldReadSourceTerm -> FieldRead.build(
                outerScope = outerScope,
                term = term,
            )

            is IntLiteralSourceTerm -> IntLiteral.build(
                outerScope = outerScope,
                term = term,
            )

            is IsUndefinedCheckSourceTerm -> IsUndefinedCheck.build(
                outerScope = outerScope,
                term = term,
            )

            is LetExpressionSourceTerm -> LetExpression.build(
                outerScope = outerScope,
                term = term,
            )

            is ReferenceSourceTerm -> Reference.build(
                outerScope = outerScope,
                term = term,
            )

            is TupleConstructorSourceTerm -> TupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is DictConstructorSourceTerm -> DictConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is SetConstructorSourceTerm -> SetConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is IfExpressionSourceTerm -> IfExpression.build(
                outerScope = outerScope,
                term = term,
            )

            is TupleTypeConstructorSourceTerm -> TupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is ArrayTypeConstructorSourceTerm -> ArrayTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is DictTypeConstructorSourceTerm -> DictTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is FunctionTypeConstructorSourceTerm -> FunctionTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )


            is InfixCallSourceTerm -> Call.build(
                outerScope = outerScope,
                term = term,
            )

            is GenericTypeConstructorSourceTerm -> TODO()
            is ParenSourceTerm -> TODO()
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

    abstract val errors: Set<SemanticError>

    protected abstract val term: ExpressionSourceTerm?

    abstract val inferredType: Thunk<Type>

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
        scope: Scope,
    ): Value? = bind(scope = scope).evaluateValueHacky(context = context)

    abstract fun bind(
        scope: Scope,
    ): Thunk<Value>

    fun bindTranslated(
        staticScope: StaticScope,
    ): Thunk<Value> = bind(
        scope = TranslationScope(
            staticScope = staticScope,
        ),
    )
}
