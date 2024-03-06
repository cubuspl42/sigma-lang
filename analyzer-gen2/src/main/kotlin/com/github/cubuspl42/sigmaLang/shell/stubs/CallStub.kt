package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.concepts.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class CallStub(
    private val calleeStub: ExpressionStub<*>,
    private val passedArgumentStub: ExpressionStub<*>,
) : ExpressionStub<Call>() {
    companion object {
        fun fieldRead(
            subjectStub: ExpressionStub<*>,
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
