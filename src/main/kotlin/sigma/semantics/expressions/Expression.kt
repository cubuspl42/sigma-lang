package sigma.semantics.expressions

import sigma.Computation
import sigma.Thunk
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.Entity
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.DictLiteralTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FieldReadTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import sigma.syntax.expressions.LetExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.SymbolLiteralTerm
import sigma.syntax.expressions.TupleLiteralTerm
import sigma.evaluation.scope.Scope

abstract class Expression : Entity() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: ExpressionTerm,
        ): Expression = when (term) {
            is AbstractionTerm -> Abstraction.build(
                outerTypeScope = typeScope,
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is CallTerm -> Call.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is FieldReadTerm -> TODO()

            is IntLiteralTerm -> IntLiteral.build(
                term = term,
            )

            is IsUndefinedCheckTerm -> IsUndefinedCheck.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is LetExpressionTerm -> LetExpression.build(
                typeScope = typeScope,
                outerDeclarationScope = declarationScope,
                term = term,
            )

            is ReferenceTerm -> Reference.build(
                declarationScope = declarationScope,
                term = term,
            )

            is SymbolLiteralTerm -> TODO()

            is TupleLiteralTerm -> TupleLiteral.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is DictLiteralTerm -> DictLiteral.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )
        }

        fun parse(
            source: String,
        ): Expression {
            val term = ExpressionTerm.parse(source = source)

            return Expression.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )
        }
    }

    val location: SourceLocation
        get() = term.location

    protected abstract val term: ExpressionTerm

    abstract val inferredType: Computation<Type>

    fun evaluate(scope: Scope): Thunk {
        return term.evaluate(scope = scope)
    }
}
