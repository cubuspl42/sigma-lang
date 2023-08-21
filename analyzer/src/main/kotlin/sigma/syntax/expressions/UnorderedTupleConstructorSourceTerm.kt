package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

data class UnorderedTupleConstructorSourceTerm(
    override val location: SourceLocation,
    override val entries: List<Entry>,
) : TupleConstructorSourceTerm(), UnorderedTupleConstructorTerm {

    companion object {
        fun build(
            ctx: UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorSourceTerm = UnorderedTupleConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleAssociation().map {
                Entry.build(it)
            },
        )
    }

    data class Entry(
        override val name: Symbol,
        override val value: ExpressionSourceTerm,
    ) : UnorderedTupleConstructorTerm.Entry {
        companion object {
            fun build(
                ctx: SigmaParser.UnorderedTupleAssociationContext,
            ): UnorderedTupleConstructorSourceTerm.Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionSourceTerm.build(ctx.value),
            )

            fun build(
                ctx: SigmaParser.UnorderedTupleTypeEntryContext,
            ): UnorderedTupleConstructorSourceTerm.Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionSourceTerm.build(ctx.valueType),
            )
        }
    }

    override fun dump(): String = "(unordered tuple constructor)"
}
