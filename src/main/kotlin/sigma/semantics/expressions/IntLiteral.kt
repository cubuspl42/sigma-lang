package sigma.semantics.expressions


import sigma.Computation
import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.semantics.SemanticError
import sigma.syntax.SourceLocation
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.Type
import sigma.syntax.expressions.IntLiteralTerm
import sigma.values.IntValue
import sigma.values.Value
import sigma.values.tables.Scope

data class IntLiteral(
    override val term: IntLiteralTerm,
) : Expression() {
    companion object {
        fun build(
            term: IntLiteralTerm,
        ): IntLiteral = IntLiteral(
            term = term,
        )
    }

    val value: IntValue
        get() = term.value

    override val inferredType: Computation<Type> = Computation.pure(
        IntLiteralType(
            value = value,
        )
    )

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
