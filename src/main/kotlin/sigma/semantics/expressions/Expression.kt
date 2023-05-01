package sigma.semantics.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.Computation
import sigma.TypeScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
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

abstract class Expression : Entity() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: ExpressionTerm,
        ): Expression = when (term) {
            is AbstractionTerm -> Abstraction.build(
                outerTypeScope = typeScope,
                declarationScope = declarationScope,
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

            is IsUndefinedCheckTerm -> TODO()

            is LetExpressionTerm -> TODO()

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

            is DictLiteralTerm -> TODO()
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
}
