package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.ArgumentScope
import com.github.cubuspl42.sigmaLang.shell.scope.Scope

data class AbstractionConstructorTerm(
    val argumentType: UnorderedTupleTypeConstructorTerm,
    val image: ExpressionTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.AbstractionConstructorContext,
        ): AbstractionConstructorTerm = AbstractionConstructorTerm(
            argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
            image = ExpressionTerm.build(ctx.image),
        )
    }

    override fun construct(context: ConstructionContext): Lazy<Expression> = object {
        val innerScope: Scope by lazy {
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
