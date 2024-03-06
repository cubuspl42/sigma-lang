package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.LocalScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

class LocalScopeStub private constructor(
    private val   buildScope: (KnotReference) -> UnorderedTupleConstructorStub,
) : ExpressionStub<KnotConstructor>() {
    data class DefinitionStub(
        val key: Identifier,
        val initializerStub: ExpressionStub<*>,
    )

    companion object {
        val resultIdentifier = Identifier(name = "__result__")

        fun of(
            definitions: Set<DefinitionStub>,
        ): ExpressionStub<KnotConstructor> = ofDefinitions(
            buildDefinitions = { definitions },
        )

        // TODO: Nuke?
        fun ofDefinitions(
            buildDefinitions: (KnotReference) -> Set<DefinitionStub>,
        ): ExpressionStub<KnotConstructor> = of(
            buildScope = { knotReference ->
                UnorderedTupleConstructorStub.fromEntries(
                    buildDefinitions(knotReference).map {
                        UnorderedTupleConstructorStub.Entry(
                            key = it.key,
                            valueStub = it.initializerStub,
                        )
                    },
                )
            },
        )

        fun of(
            buildScope: (KnotReference) -> UnorderedTupleConstructorStub,
        ): ExpressionStub<KnotConstructor> = LocalScopeStub(
            buildScope = buildScope,
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
            fieldName = resultIdentifier,
        )
    }

    override fun form(
        context: FormationContext,
    ) = lazyOf(
        KnotConstructor.of { knotReference ->
            val localScope = buildScope(knotReference)

            val innerScope = LocalScope(
                names = localScope.keys,
                reference = knotReference,
            ).chainWith(
                context.scope,
            )

            val innerContext = context.copy(
                scope = innerScope,
            )

            localScope.formStrict(
                context = innerContext,
            )
        },
    )
}
