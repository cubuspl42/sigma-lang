package sigma.semantics.expressions

import sigma.evaluation.Thunk
import sigma.evaluation.scope.Scope
import sigma.semantics.Computation
import sigma.semantics.DeclarationScope
import sigma.semantics.Entity
import sigma.semantics.TypeScope
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.DictConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FieldReadTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import sigma.syntax.expressions.LetExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.SetConstructorTerm
import sigma.syntax.expressions.SymbolLiteralTerm
import sigma.syntax.expressions.TupleConstructorTerm

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

            is FieldReadTerm -> FieldRead.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

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

            is TupleConstructorTerm -> TupleConstructor.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is DictConstructorTerm -> DictConstructor.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is SetConstructorTerm -> SetConstructor.build(
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
    fun bind(scope: Scope): Thunk = evaluate(scope = scope)

    abstract fun evaluate(
        scope: Scope,
    ): Thunk
}
