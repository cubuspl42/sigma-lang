package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.IdentifierLiteral
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class CallStub(
    private val calleeStub: ExpressionStub,
    private val passedArgumentStub: ExpressionStub,
) : ExpressionStub() {
    companion object {
        fun fieldRead(
            subjectStub: ExpressionStub,
            readFieldName: Identifier,
        ) = CallStub(
            calleeStub = subjectStub,
            passedArgumentStub = IdentifierLiteral(
                value = readFieldName,
            ).asStub(),
        )

        val panicCall = CallStub(
            calleeStub = referBuiltin(
                name = Identifier(name = "panic"),
            ),
            passedArgumentStub = UnorderedTupleConstructor.Empty.asStub(),
        )
    }

    override fun form(context: FormationContext): Lazy<Expression> = lazyOf(
        Call(
            calleeLazy = calleeStub.form(context = context),
            passedArgumentLazy = passedArgumentStub.form(context = context),
        )
    )
}
