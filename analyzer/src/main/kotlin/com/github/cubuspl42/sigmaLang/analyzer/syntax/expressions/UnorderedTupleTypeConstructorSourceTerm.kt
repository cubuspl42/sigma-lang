package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class UnorderedTupleTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val entries: List<Entry>,
) : TupleTypeConstructorSourceTerm(), UnorderedTupleTypeConstructorTerm {
    data class Entry(
        override val name: Identifier,
        override val type: ExpressionTerm,
    ) : UnorderedTupleTypeConstructorTerm.Entry {
        companion object {
            fun build(
                ctx: SigmaParser.UnorderedTupleTypeEntryContext,
            ): Entry = Entry(
                name = Identifier.of(ctx.name.text),
                type = ExpressionSourceTerm.build(ctx.valueType),
            )
        }
    }

    companion object {
        fun build(
            ctx: UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorSourceTerm = UnorderedTupleTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleTypeEntry().map {
                Entry.build(it)
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
