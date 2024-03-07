package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class ReferenceStub(
    private val referredName: Identifier,
) : ExpressionStub<Expression>() {
    override fun form(context: FormationContext): Lazy<Expression> {
        val scope = context.scope

        val reference = scope.resolveName(referredName = referredName)
            ?: throw IllegalStateException("Unresolved reference: $referredName")

        return CallStub.fieldRead(
            subjectStub = reference.asStub(),
            fieldName = referredName,
        ).form(
            context = context,
        )
    }
}
