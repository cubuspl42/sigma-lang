package sigma.syntax.typeExpressions

import sigma.semantics.TypeScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.semantics.types.TypeEntity

data class UnorderedTupleTypeConstructorTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleTypeConstructorTerm() {
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
            ctx: UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
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
        typeScope: TypeScope,
    ): UnorderedTupleType = UnorderedTupleType(
        valueTypeByName = entries.groupBy {
            it.name
        }.mapValues { (key, entryTypes) ->
            val valueTypes = entryTypes.map {
                it.valueType.evaluateAsType(typeScope = typeScope)
            }

            valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
        },
    )
}
