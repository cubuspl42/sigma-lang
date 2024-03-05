package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ArgumentScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

class AbstractionConstructorStub(
    private val argumentNames: Set<Identifier>,
    private val image: ExpressionStub,
) : ExpressionStub() {
    override fun form(context: FormationContext): Lazy<AbstractionConstructor> {
        val abstractionConstructor = LazyUtils.looped { abstractionConstructorLooped ->
            val innerContext = context.copy(
                scope = ArgumentScope(
                    argumentNames = argumentNames,
                    abstractionConstructorLazy = abstractionConstructorLooped,
                ).chainWith(context.scope),
            )

            return@looped AbstractionConstructor(
                body = image.form(context = innerContext).value,
            )
        }

        return lazyOf(abstractionConstructor)
    }
}
