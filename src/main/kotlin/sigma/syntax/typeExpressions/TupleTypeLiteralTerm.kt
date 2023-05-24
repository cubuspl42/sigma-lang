package sigma.syntax.typeExpressions

import sigma.Thunk
import sigma.TypeScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.values.tables.Table
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleTypeLiteralEntryContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.TupleType
import sigma.syntax.SourceLocation

data class TupleTypeLiteralTerm(
    override val location: SourceLocation,
    val orderedEntries: List<Entry>,
    val unorderedEntries: List<Entry>,
) : TypeExpressionTerm() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeErrorException(
        message = "Duplicate key: ${key.dump()}",
    )

    data class Entry(
        val name: Symbol,
        val type: TypeExpressionTerm,
    ) {
        companion object {
            fun build(ctx: TupleTypeLiteralEntryContext): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                type = TypeExpressionTerm.build(ctx.valueType),
            )
        }
    }

    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeLiteralContext,
        ): TupleTypeLiteralTerm {
            val location = SourceLocation.build(ctx)

            return object : SigmaParserBaseVisitor<TupleTypeLiteralTerm>() {
                override fun visitEmptyTupleTypeLiteralAlt(
                    ctx: SigmaParser.EmptyTupleTypeLiteralAltContext,
                ): TupleTypeLiteralTerm = TupleTypeLiteralTerm(
                    location = location,
                    orderedEntries = emptyList(),
                    unorderedEntries = emptyList(),
                )

                override fun visitOrderedTupleTypeLiteralAlt(
                    ctx: SigmaParser.OrderedTupleTypeLiteralAltContext,
                ): TupleTypeLiteralTerm = TupleTypeLiteralTerm(
                    location = location,
                    orderedEntries = ctx.tupleTypeLiteralOrderedPart().entries.map { Entry.build(it) },
                    unorderedEntries = emptyList(),
                )

                override fun visitUnorderedTupleTypeLiteralAlt(
                    ctx: SigmaParser.UnorderedTupleTypeLiteralAltContext,
                ): TupleTypeLiteralTerm = TupleTypeLiteralTerm(
                    location = location,
                    orderedEntries = emptyList(),
                    unorderedEntries = ctx.tupleTypeLiteralUnorderedPart().entries.map { Entry.build(it) },
                )

                override fun visitMixedTupleTypeLiteralAlt(
                    ctx: SigmaParser.MixedTupleTypeLiteralAltContext,
                ): TupleTypeLiteralTerm = TupleTypeLiteralTerm(
                    location = location,
                    orderedEntries = ctx.tupleTypeLiteralOrderedPart().entries.map { Entry.build(it) },
                    unorderedEntries = ctx.tupleTypeLiteralUnorderedPart().entries.map { Entry.build(it) },
                )
            }.visit(ctx) ?: throw IllegalArgumentException("Can't match type expression ${ctx::class}")
        }
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): TupleType = TupleType(
        orderedEntries = orderedEntries.mapIndexed { index, entry ->
            TupleType.OrderedEntry(
                index = index,
                name = entry.name,
                type = entry.type.evaluate(typeScope = typeScope),
            )
        },
        unorderedEntries = unorderedEntries.groupBy {
            it.name
        }.map { (key, entryTypes) ->
            val valueTypes = entryTypes.map {
                it.type.evaluate(typeScope = typeScope)
            }

            valueTypes.singleOrNull()?.let {
                TupleType.UnorderedEntry(
                    name = key,
                    type = it,
                )
            } ?: throw DuplicateKeyError(key = key)
        }.toSet(),
    )

    fun toArgumentScope(argument: Table): Scope = object : Scope {
        override fun getValue(name: Symbol): Thunk? {
            val orderedIndexedEntry = orderedEntries.withIndex().singleOrNull { it.value.name == name }

            if (orderedIndexedEntry != null) {
                return argument.read(
                    IntValue(value = orderedIndexedEntry.index.toLong()),
                )
            }

            val unorderedEntry = unorderedEntries.singleOrNull { it.name == name }

            if (unorderedEntry != null) {
                return argument.read(unorderedEntry.name)
            }

            return null
        }
    }
}
