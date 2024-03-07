package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class CallStub(
    private val calleeStub: ExpressionStub<ShadowExpression>,
    private val passedArgumentStub: ExpressionStub<ShadowExpression>,
) : ExpressionStub<Call>() {
    companion object {
        fun fieldRead(
            subjectStub: ExpressionStub<ShadowExpression>,
            fieldName: Identifier,
        ) = subjectStub.map {
            it.rawExpression.readField(fieldName = fieldName)
        }
    }

    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<Call> = Call.builder(
        calleeBuilder = calleeStub.transform(context = context),
        passedArgumentBuilder = passedArgumentStub.transform(context = context),
    )
}
