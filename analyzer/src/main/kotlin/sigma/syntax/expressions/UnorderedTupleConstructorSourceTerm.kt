package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

data class UnorderedTupleConstructorSourceTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleConstructorSourceTerm() {
    data class Entry(
        val name: Symbol,
        val value: ExpressionSourceTerm,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.UnorderedTupleAssociationContext,
            ): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionSourceTerm.build(ctx.value),
            )

            fun build(
                ctx: SigmaParser.UnorderedTupleTypeEntryContext,
            ): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionSourceTerm.build(ctx.valueType),
            )
        }
    }

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

    override fun dump(): String = "(dict constructor)"
}
