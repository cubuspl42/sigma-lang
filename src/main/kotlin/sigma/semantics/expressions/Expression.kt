package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.ArrayTypeConstructorTerm
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.DictConstructorTerm
import sigma.syntax.expressions.DictTypeTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FieldReadTerm
import sigma.syntax.expressions.FunctionTypeTerm
import sigma.syntax.expressions.GenericTypeConstructorTerm
import sigma.syntax.expressions.IfExpressionTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import sigma.syntax.expressions.LetExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.SetConstructorTerm
import sigma.syntax.expressions.SymbolLiteralTerm
import sigma.syntax.expressions.TupleConstructorTerm
import sigma.syntax.expressions.TupleTypeConstructorTerm

abstract class Expression {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: ExpressionTerm,
        ): Expression = when (term) {
            is AbstractionTerm -> Abstraction.build(
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is CallTerm -> Call.build(
                declarationScope = declarationScope,
                term = term,
            )

            is FieldReadTerm -> FieldRead.build(
                declarationScope = declarationScope,
                term = term,
            )

            is IntLiteralTerm -> IntLiteral.build(
                term = term,
            )

            is IsUndefinedCheckTerm -> IsUndefinedCheck.build(
                declarationScope = declarationScope,
                term = term,
            )

            is LetExpressionTerm -> LetExpression.build(
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is ReferenceTerm -> Reference.build(
                declarationScope = declarationScope,
                term = term,
            )

            is SymbolLiteralTerm -> TODO()

            is TupleConstructorTerm -> TupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is DictConstructorTerm -> DictConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is SetConstructorTerm -> SetConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is IfExpressionTerm -> IfExpression.build(
                declarationScope = declarationScope,
                term = term,
            )

            is TupleTypeConstructorTerm -> TupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is ArrayTypeConstructorTerm -> ArrayTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is DictTypeTerm -> TODO()

            is FunctionTypeTerm -> TODO()

            is GenericTypeConstructorTerm -> TODO()
        }

        fun parse(
            source: String,
        ): Expression {
            val term = ExpressionTerm.parse(source = source)

            return Expression.build(
                declarationScope = StaticScope.Empty,
                term = term,
            )
        }
    }

    val location: SourceLocation
        get() = term.location

    abstract val errors: Set<SemanticError>

    protected abstract val term: ExpressionTerm

    abstract val inferredType: Computation<Type>

    abstract fun evaluate(
        scope: Scope,
    ): EvaluationResult
}
