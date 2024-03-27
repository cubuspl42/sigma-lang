package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class UnorderedTupleConstructorTerm(
    val entries: List<Entry>,
) : TupleConstructorTerm() {
    data class Entry(
        val key: IdentifierTerm,
        val value: ExpressionTerm,
    ) : Wrappable {
        override fun wrap(): Value = UnorderedTupleValue(
            valueByKey = mapOf(
                Identifier.of("key") to lazyOf(key.wrap()),
                Identifier.of("value") to lazyOf(value.wrap()),
            ),
        )
    }

    companion object : Term.Builder<SigmaParser.UnorderedTupleConstructorContext, UnorderedTupleConstructorTerm>() {
        val Empty: UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = emptyList(),
        )

        override fun build(
            ctx: SigmaParser.UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = ctx.unorderedTupleConstructorEntry().map {
                Entry(
                    key = IdentifierTerm.build(it.key),
                    value = ExpressionTerm.build(it.value),
                )
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.UnorderedTupleConstructorContext =
            parser.unorderedTupleConstructor()
    }

    override fun transmute(): ExpressionStub<UnorderedTupleConstructor> =
        object : ExpressionStub<UnorderedTupleConstructor>() {
            override fun transform(
                context: FormationContext,
            ) = UnorderedTupleConstructor.fromEntries(
                entries = entries.map {
                    UnorderedTupleConstructor.Entry(
                        key = it.key.transmute(),
                        value = lazy {
                            it.value.transmute().build(
                                formationContext = context,
                            )
                        },
                    )
                },
            )
        }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("entries") to lazyOf(entries.wrap()),
        ),
    )
}
