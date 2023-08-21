package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import sigma.syntax.SourceLocation

data class UnorderedTupleTypeConstructorSourceTerm(
    override val location: SourceLocation,
    val entries: List<UnorderedTupleConstructorSourceTerm.Entry>,
) : TupleTypeConstructorSourceTerm() {
    companion object {
        fun build(
            ctx: UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorSourceTerm = UnorderedTupleTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleTypeEntry().map {
                UnorderedTupleConstructorSourceTerm.Entry.build(it)
            },
        )
    }

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): UnorderedTupleType = UnorderedTupleType(
//        valueTypeByName = entries.groupBy {
//            it.name
//        }.mapValues { (key, entryTypes) ->
//            val valueTypes = entryTypes.map {
//                it.valueType.evaluateAsType(declarationScope = declarationScope)
//            }
//
//            valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
//        },
//    )

    override fun dump(): String = "(unordered tuple type constructor)"
}
