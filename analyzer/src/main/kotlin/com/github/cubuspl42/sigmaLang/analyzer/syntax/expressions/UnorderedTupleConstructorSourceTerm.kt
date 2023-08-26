package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.UnorderedTupleConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

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
        override val value: ExpressionTerm,
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
