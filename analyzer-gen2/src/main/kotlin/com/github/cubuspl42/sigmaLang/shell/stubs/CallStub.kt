package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class CallStub(
    private val calleeStub: ExpressionStub<Expression>,
    private val passedArgumentStub: ExpressionStub<Expression>,
) : ExpressionStub<Call>() {
    companion object {
        fun fieldRead(
            subjectStub: ExpressionStub<Expression>,
            fieldName: Identifier,
        ) = subjectStub.map {
            it.readField(fieldName = fieldName)
        }
    }

    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<Call> = Call.builder(
        calleeBuilder = calleeStub.transform(context = context),
        passedArgumentBuilder = passedArgumentStub.transform(context = context),
    )
}
