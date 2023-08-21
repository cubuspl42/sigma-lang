package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateValueHacky
import sigma.semantics.DynamicResolution
import sigma.semantics.Formula
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.StaticResolution
import sigma.semantics.types.MetaType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionSourceTerm
import sigma.syntax.expressions.ArrayTypeConstructorSourceTerm
import sigma.syntax.expressions.CallSourceTerm
import sigma.syntax.expressions.DictConstructorSourceTerm
import sigma.syntax.expressions.DictTypeConstructorSourceTerm
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.FieldReadSourceTerm
import sigma.syntax.expressions.FunctionTypeConstructorSourceTerm
import sigma.syntax.expressions.GenericTypeConstructorSourceTerm
import sigma.syntax.expressions.IfExpressionSourceTerm
import sigma.syntax.expressions.IntLiteralSourceTerm
import sigma.syntax.expressions.IsUndefinedCheckSourceTerm
import sigma.syntax.expressions.LetExpressionSourceTerm
import sigma.syntax.expressions.ReferenceSourceTerm
import sigma.syntax.expressions.SetConstructorSourceTerm
import sigma.syntax.expressions.SymbolLiteralSourceTerm
import sigma.syntax.expressions.TupleConstructorSourceTerm
import sigma.syntax.expressions.TupleTypeConstructorSourceTerm

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
            declarationScope: StaticScope,
            term: ExpressionSourceTerm,
        ): Expression = when (term) {
            is AbstractionSourceTerm -> Abstraction.build(
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is CallSourceTerm -> Call.build(
                declarationScope = declarationScope,
                term = term,
            )

            is FieldReadSourceTerm -> FieldRead.build(
                declarationScope = declarationScope,
                term = term,
            )

            is IntLiteralSourceTerm -> IntLiteral.build(
                term = term,
            )

            is IsUndefinedCheckSourceTerm -> IsUndefinedCheck.build(
                declarationScope = declarationScope,
                term = term,
            )

            is LetExpressionSourceTerm -> LetExpression.build(
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is ReferenceSourceTerm -> Reference.build(
                declarationScope = declarationScope,
                term = term,
            )

            is SymbolLiteralSourceTerm -> TODO()

            is TupleConstructorSourceTerm -> TupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is DictConstructorSourceTerm -> DictConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is SetConstructorSourceTerm -> SetConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is IfExpressionSourceTerm -> IfExpression.build(
                declarationScope = declarationScope,
                term = term,
            )

            is TupleTypeConstructorSourceTerm -> TupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is ArrayTypeConstructorSourceTerm -> ArrayTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is DictTypeConstructorSourceTerm -> DictTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is FunctionTypeConstructorSourceTerm -> FunctionTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is GenericTypeConstructorSourceTerm -> TODO()
        }

        fun parse(
            source: String,
        ): Expression {
            val term = ExpressionSourceTerm.parse(source = source)

            return Expression.build(
                declarationScope = StaticScope.Empty,
                term = term,
            )
        }
    }

    val location: SourceLocation
        get() = term.location

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
