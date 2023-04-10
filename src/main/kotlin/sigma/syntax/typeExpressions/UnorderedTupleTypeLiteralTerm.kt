package sigma.syntax.typeExpressions

import sigma.SyntaxTypeScope
import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralContext
import sigma.semantics.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope
import sigma.values.tables.Table

data class UnorderedTupleTypeLiteralTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleTypeLiteralTerm() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeError(
        message = "Duplicate key: ${key.dump()}",
    )

    data class Entry(
        val name: Symbol,
        val valueType: TypeExpressionTerm,
    )

    companion object {
        fun build(
            ctx: UnorderedTupleTypeLiteralContext,
        ): UnorderedTupleTypeLiteralTerm = UnorderedTupleTypeLiteralTerm(
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
        typeScope: SyntaxTypeScope,
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
