package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
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

            is UnionTypeConstructorTerm -> TODO()

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

    abstract val errors: Set<SemanticError>

    protected abstract val term: ExpressionTerm?

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
