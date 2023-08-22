package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.evaluateValueHacky
import com.github.cubuspl42.sigmaLang.analyzer.semantics.DynamicResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.CallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SymbolLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorSourceTerm

data class EvaluationContext(
    val evaluationDepth: Int,
) {
    companion object {
        val Initial: EvaluationContext = EvaluationContext(
            evaluationDepth = 0,
        )

        const val maxEvaluationDepth: Int = 2048
    }

    fun withIncreasedDepth(): EvaluationContext = EvaluationContext(
        evaluationDepth = evaluationDepth + 1,
    )
}

class TranslationScope(
    private val staticScope: StaticScope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = staticScope.resolveName(
        name = name,
    )?.let { resolvedName ->
        when (val resolution = resolvedName.resolution) {
            is StaticResolution -> resolution.resolvedValue

            is DynamicResolution -> when (resolvedName.type.value) {
                is MetaType -> TypeVariable(
                    // FIXME
                    formula = resolution.resolvedFormula ?: Formula(name = Symbol.of("?")),
                ).asThunk

                else -> null
            }
        }
    }
}

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

            is CallSourceTerm -> Call.build(
                outerScope = outerScope,
                term = term,
            )

            is FieldReadSourceTerm -> FieldRead.build(
                outerScope = outerScope,
                term = term,
            )

            is IntLiteralSourceTerm -> IntLiteral.build(
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

            is SymbolLiteralSourceTerm -> TODO()

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

            is GenericTypeConstructorSourceTerm -> TODO()
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

    val location: SourceLocation
        get() = term.location

    abstract val outerScope: StaticScope

    abstract val errors: Set<SemanticError>

    protected abstract val term: ExpressionSourceTerm

    abstract val inferredType: Thunk<Type>

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
