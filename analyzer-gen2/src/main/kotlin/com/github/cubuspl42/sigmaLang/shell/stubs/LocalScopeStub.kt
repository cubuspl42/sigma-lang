package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.LocalScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

class LocalScopeStub(
    private val definitions: Set<DefinitionStub>,
    private val result: ExpressionStub<*>,
) : ExpressionStub<KnotConstructor>() {
    data class DefinitionStub(
        val key: Identifier,
        val initializer: ExpressionStub<*>,
    )

    companion object {
        val resultIdentifier = Identifier(name = "__result__")
    }

    override fun form(
        context: FormationContext,
    ): Lazy<Expression> = lazyOf(
        KnotConstructor.of { knotReference ->
            val innerScope = LocalScope(
                names = definitions.mapUniquely { it.key },
                reference = knotReference,
            ).chainWith(
                context.scope,
            )

            val innerContext = context.copy(
                scope = innerScope,
            )

            UnorderedTupleConstructorStub(
                valueStubByKey = definitions.associate {
                    it.key to it.initializer
                } + mapOf(
                    resultIdentifier to result,
                ),
            ).form(
                context = innerContext,
            ).value
        },
    )
}
