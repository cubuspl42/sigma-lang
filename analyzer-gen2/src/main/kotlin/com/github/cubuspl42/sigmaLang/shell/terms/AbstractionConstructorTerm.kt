package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext
import com.github.cubuspl42.sigmaLang.shell.scope.FieldScope
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

data class AbstractionConstructorTerm(
    val argumentType: TupleTypeConstructorTerm,
    val image: ExpressionTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.AbstractionConstructorContext, AbstractionConstructorTerm>() {
        override fun build(
            ctx: SigmaParser.AbstractionConstructorContext,
        ): AbstractionConstructorTerm = AbstractionConstructorTerm(
            argumentType = TupleTypeConstructorTerm.build(ctx.argumentType),
            image = ExpressionTerm.build(ctx.image),
        )

        fun build(
            argumentTypeCtx: SigmaParser.TupleTypeConstructorContext,
            bodyCtx: SigmaParser.ExpressionContext,
        ): AbstractionConstructorTerm = AbstractionConstructorTerm(
            argumentType = TupleTypeConstructorTerm.build(argumentTypeCtx),
            image = ExpressionTerm.build(bodyCtx),
        )

        override fun extract(parser: SigmaParser): SigmaParser.AbstractionConstructorContext =
            parser.abstractionConstructor()
    }

    val argumentNames: Set<Identifier>
        get() = argumentType.names.toSet()

    fun build(
        transmutationContext: TransmutationContext,
        extraArgumentNames: Set<Identifier> = emptySet(),
    ): AbstractionConstructor = AbstractionConstructor.looped1 { argumentReference ->
        image.transmute(
            context = transmutationContext.extendScope(
                innerScope = FieldScope(
                    names = argumentNames + extraArgumentNames,
                    tupleReference = argumentReference,
                ),
            ),
        )
    }

    override fun transmute(context: TransmutationContext): Expression {
        val argumentReference = AbstractionConstructor.looped1 { argumentReference ->
            val innerScope = StaticScope.argumentScope(
                argumentNames = argumentNames,
                argumentReference = argumentReference,
            ).chainWith(
                context.scope,
            )

            val innerContext = context.copy(
                scope = innerScope,
            )

            image.transmute(
                context = innerContext,
            )
        }

        return argumentReference
    }

    override fun wrap(): UnorderedTupleValue = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("name") to lazyOf(argumentType.wrap()),
            Identifier.of("body") to lazyOf(image.wrap()),
        ),
    )
}
