package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

data class UnorderedTupleConstructorTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleConstructorTerm() {
    data class Entry(
        val name: Symbol,
        val value: ExpressionTerm,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.UnorderedTupleAssociationContext,
            ): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionTerm.build(ctx.value),
            )

            fun build(
                ctx: SigmaParser.UnorderedTupleTypeEntryContext,
            ): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionTerm.build(ctx.valueType),
            )
        }
    }

    companion object {
        fun build(
            ctx: UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleAssociation().map {
                Entry.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"
}
