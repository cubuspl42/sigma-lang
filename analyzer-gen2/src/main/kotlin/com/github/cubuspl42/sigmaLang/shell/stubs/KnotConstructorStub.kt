package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.KnotScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

class KnotConstructorStub(
    private val definitions: Set<DefinitionStub>,
    private val result: ExpressionStub<*>,
) : ExpressionStub<KnotConstructor>() {
    data class DefinitionStub(
        val key: Identifier,
        val initializer: ExpressionStub<*>,
    )

    override fun form(context: FormationContext): Lazy<Expression> {
        val knotConstructor = LazyUtils.looped { knotConstructorLooped ->
            val innerContext = context.copy(
                scope = KnotScope(
                    knotConstructorLazy = knotConstructorLooped,
                ).chainWith(context.scope),
            )

            return@looped KnotConstructor(
                definitionByIdentifier = definitions.associate {
                    it.key to it.initializer.form(
                        context = innerContext,
                    )
                },
                resultLazy = result.form(
                    context = innerContext,
                ),
            )
        }

        return lazyOf(knotConstructor)
    }
}
