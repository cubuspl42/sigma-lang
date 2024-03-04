package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.ArgumentScope
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope

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

    override fun construct(context: ConstructionContext): Lazy<AbstractionConstructor> = object {
        val innerScope: StaticScope by lazy {
            ArgumentScope.construct(
                abstractionConstructorLazy = abstractionConstructorLazy,
                argumentType = argumentType,
            )
        }

        val abstractionConstructorLazy = lazy {
            AbstractionConstructor(
                body = image.construct(
                    context.copy(
                        scope = innerScope,
                    )
                ).value,
            )
        }
    }.abstractionConstructorLazy
}
