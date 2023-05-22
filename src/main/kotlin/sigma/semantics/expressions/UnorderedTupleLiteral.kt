package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.parser.antlr.SigmaParser
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.OrderedTupleLiteralTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.evaluation.values.Symbol

class UnorderedTupleLiteral(
    override val term: UnorderedTupleLiteralTerm,
    val elements: List<Entry>,
) : TupleLiteral() {
    data class Entry(
        val name: Symbol,
        val value: Expression,
    ) {
        companion object {
            fun build(
                typeScope: TypeScope,
                declarationScope: DeclarationScope,
                entry: UnorderedTupleLiteralTerm.Entry,
            ): Entry = Entry(
                name = entry.name,
                value = Expression.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    term = entry.value,
                ),
            )
        }
    }

    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: UnorderedTupleLiteralTerm,
        ): UnorderedTupleLiteral = UnorderedTupleLiteral(
            term = term,
            elements = term.entries.map {
                Entry.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    entry = it,
                )
            },
        )
    }

    override val inferredType: Computation<Type>
        get() = TODO("Not yet implemented")

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}

