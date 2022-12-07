package sigma.expressions

import sigma.StaticTypeScope
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralContext
import sigma.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class UnorderedTupleTypeLiteral(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TypeExpression() {
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
                    valueType = TypeExpression.build(it.valueType),
                )
            }
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): Type {
        val valueTypeByName = entries.groupBy {
            it.name
        }.mapValues { (key, entryTypes) ->
            val valueTypes = entryTypes.map {
                it.valueType.evaluate(typeScope = typeScope)
            }

            valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
        }

        return UnorderedTupleType(
            valueTypeByName = valueTypeByName,
        )
    }
}
