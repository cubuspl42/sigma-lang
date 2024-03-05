package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ArgumentScope
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.stubs.AbstractionConstructorStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class AbstractionConstructorTerm(
    val argumentType: UnorderedTupleTypeConstructorTerm,
    val image: ExpressionTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.AbstractionConstructorContext, AbstractionConstructorTerm>() {
        override fun build(
            ctx: SigmaParser.AbstractionConstructorContext,
        ): AbstractionConstructorTerm = AbstractionConstructorTerm(
            argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
            image = ExpressionTerm.build(ctx.image),
        )

        override fun extract(parser: SigmaParser): SigmaParser.AbstractionConstructorContext =
            parser.abstractionConstructor()
    }

    override fun transmute(): AbstractionConstructorStub = AbstractionConstructorStub(
        argumentNames = argumentType.keys.mapUniquely { it.transmute() },
        image = image.transmute(),
    )
}
