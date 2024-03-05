package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class ReferenceStub(
    private val referredName: Identifier,
) : ExpressionStub<Reference>() {
    override fun form(context: FormationContext): Lazy<Expression> {
        val scope = context.scope

        val reference = scope.resolveName(referredName = referredName)
            ?: throw IllegalStateException("Unresolved reference: $referredName")

        return CallStub.fieldRead(
            subjectStub = reference.asStub(),
            readFieldName = referredName,
        ).form(
            context = context,
        )
    }
}
