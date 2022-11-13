package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.TupleContext
import sigma.types.DictType
import sigma.types.PrimitiveType
import sigma.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class UnorderedTupleTypeLiteral(
    override val location: SourceLocation,
    val entries: List<EntryExpression>,
) : TypeExpression() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeError(
        message = "Duplicate key: ${key.dump()}",
    )

    data class EntryExpression(
        val name: Symbol,
        val valueType: TypeExpression,
    )

    companion object {
        fun build(
            ctx: TupleContext,
        ): UnorderedTupleTypeLiteral = TODO()
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): Type {
        val valueTypeByName: Map<PrimitiveValue, Type> = entries.groupBy {
            it.name
        }.mapValues { (key, entryTypes) ->
            val valueTypes = entryTypes.map {
                it.valueType.evaluate(typeScope = typeScope)
            }

            valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
        }

        return UnorderedTupleType(
            valueTypeByKey = valueTypeByName,
        )
    }
}
