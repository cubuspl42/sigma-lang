package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.resolveName

class ReferenceStub(
    private val referredName: Identifier,
) : ExpressionStub() {
    override fun form(context: FormationContext): Lazy<Expression> {
        val scope = context.scope

        return lazy {
            when (val resolution = scope.resolveName(referredName = referredName)) {
                is StaticScope.ResolvedReference -> CallStub.fieldRead(
                    subjectStub = when (resolution) {
                        is StaticScope.ArgumentReference -> ArgumentReference(
                            referredAbstractionLazy = resolution.referredAbstractionLazy,
                        ).asStub()

                        is StaticScope.DefinitionReference -> KnotReference(
                            referredKnotLazy = resolution.referredKnotLazy,
                        ).asStub()
                    },
                    readFieldName = referredName,
                )

                StaticScope.UnresolvedReference -> throw IllegalStateException("Unresolved reference: $referredName")
            }.form(
                context = context,
            ).value
        }
    }
}
