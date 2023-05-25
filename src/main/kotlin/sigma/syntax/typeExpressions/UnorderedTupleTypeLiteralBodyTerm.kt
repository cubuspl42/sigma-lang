package sigma.syntax.typeExpressions

import sigma.TypeScope
import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralBodyContext
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.tables.Table

data class UnorderedTupleTypeLiteralBodyTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleTypeLiteralBodyTerm() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeErrorException(
        message = "Duplicate key: ${key.dump()}",
    )

    data class Entry(
        val name: Symbol,
        val valueType: TypeExpressionTerm,
    )

    companion object {
        fun build(
            ctx: UnorderedTupleTypeLiteralBodyContext,
        ): UnorderedTupleTypeLiteralBodyTerm = UnorderedTupleTypeLiteralBodyTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleTypeEntry().map {
                Entry(
                    name = Symbol.of(it.name.text),
                    valueType = TypeExpressionTerm.build(it.valueType),
                )
            }
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
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
        override fun getValue(name: Symbol): Thunk? = argument.read(name)
    }
}
