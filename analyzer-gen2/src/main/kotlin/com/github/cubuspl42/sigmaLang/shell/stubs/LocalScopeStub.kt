package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.LocalScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

class LocalScopeStub private constructor(
    private val definitions: Set<DefinitionStub>,
) : ExpressionStub<KnotConstructor>() {
    data class DefinitionStub(
        val key: Identifier,
        val initializerStub: ExpressionStub<*>,
    )

    companion object {
        val resultIdentifier = Identifier(name = "__result__")

        fun of(
            definitions: Set<DefinitionStub>,
        ): ExpressionStub<KnotConstructor> = LocalScopeStub(
            definitions = definitions,
        )

        fun of(
            definitions: Set<DefinitionStub>,
            result: ExpressionStub<*>,
        ): ExpressionStub<Call> = CallStub.fieldRead(
            subjectStub = of(
                definitions = definitions + DefinitionStub(
                    key = resultIdentifier,
                    initializerStub = result,
                ),
            ),
            readFieldName = resultIdentifier,
        )
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

            UnorderedTupleConstructor(
                valueByKey = definitions.associate {
                    it.key to it.initializerStub.form(
                        context = innerContext,
                    )
                },
            )
        },
    )
}
