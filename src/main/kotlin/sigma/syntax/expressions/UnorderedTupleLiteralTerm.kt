package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleLiteralContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable
import sigma.values.tables.Scope
import sigma.semantics.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class UnorderedTupleLiteralTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleLiteralTerm() {
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
        }
    }

    // TODO: Share with `UnorderedTupleType`
    data class EntryType(
        val name: Symbol,
        val valueType: Type,
    )

    class DuplicatedNameError(
        duplicatedKey: PrimitiveValue,
    ) : TypeError(
        message = "Duplicate key: ${duplicatedKey.dump()}",
    )

    companion object {
        fun build(
            ctx: UnorderedTupleLiteralContext,
        ): UnorderedTupleLiteralTerm = UnorderedTupleLiteralTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleAssociation().map {
                Entry.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): UnorderedTupleType {
        val entryTypes = entries.map {
            val valueType = it.value.determineType(
                typeScope = typeScope,
                valueScope = valueScope,
            )

            EntryType(
                name = it.name,
                valueType = valueType,
            )
        }

        val entryTypeByName = entryTypes.groupBy { it.name }

        return UnorderedTupleType(
            valueTypeByName = entryTypeByName.mapValues { (name, entryTypes) ->
                val entryType = entryTypes.singleOrNull() ?: throw DuplicatedNameError(
                    duplicatedKey = name,
                )

                entryType.valueType
            },
        )
    }

    override fun evaluate(
        scope: Scope,
    ): DictTable = DictTable(
        entries = entries.associate {
            it.name to it.value.bind(scope = scope)
        },
    )
}
