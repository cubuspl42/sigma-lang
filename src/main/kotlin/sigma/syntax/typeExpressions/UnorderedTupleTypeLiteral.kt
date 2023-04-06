package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralContext
import sigma.semantics.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope
import sigma.values.tables.Table

data class UnorderedTupleTypeLiteral(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleTypeLiteral() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeError(
        message = "Duplicate key: ${key.dump()}",
    )

    data class Entry(
        val name: Symbol,
        val valueType: TypeExpression,
    )

    companion object {
        fun build(
            ctx: UnorderedTupleTypeLiteralContext,
        ): UnorderedTupleTypeLiteral = UnorderedTupleTypeLiteral(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleTypeEntry().map {
                Entry(
                    name = Symbol.of(it.name.text),
                    valueType = build(it.valueType),
                )
            }
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): UnorderedTupleType = UnorderedTupleType(
        valueTypeByName = entries.groupBy {
            it.name
        }.mapValues { (key, entryTypes) ->
            val valueTypes = entryTypes.map {
                it.valueType.evaluate(typeScope = typeScope)
            }

            valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
        },
    )

    override fun toArgumentScope(argument: Table): Scope = object : Scope {
        override fun get(name: Symbol): Thunk? = argument.read(name)
    }
}
