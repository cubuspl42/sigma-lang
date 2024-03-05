package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

abstract class ExpressionStub {
    data class IfFunctionStub(
        val calleeStub: ExpressionStub,
    ) {
        fun call(
            condition: ExpressionStub,
            thenCase: ExpressionStub,
            elseCase: ExpressionStub,
        ): ExpressionStub = CallStub(
            calleeStub = calleeStub,
            passedArgumentStub = UnorderedTupleConstructorStub(
                valueStubByKey = mapOf(
                    Identifier(name = "condition") to condition,
                    Identifier(name = "then") to thenCase,
                    Identifier(name = "else") to elseCase,
                ),
            ),
        )
    }

    companion object {
        fun referBuiltin(name: Identifier): ExpressionStub = object : ExpressionStub() {
            override fun form(context: FormationContext) = CallStub.fieldRead(
                subjectStub = CallStub.fieldRead(
                    subjectStub = ArgumentReference(
                        referredAbstractionLazy = context.moduleRoot,
                    ).asStub(),

                    readFieldName = Identifier(
                        name = "builtin",
                    ),
                ),
                readFieldName = name,
            ).form(context = context)
        }

        val ifFunction: IfFunctionStub = IfFunctionStub(
            calleeStub = referBuiltin(
                name = Identifier(name = "if"),
            ),
        )
    }

    abstract fun form(
        context: FormationContext,
    ): Lazy<Expression>
}

fun Expression.asStub(): ExpressionStub = object : ExpressionStub() {
    override fun form(context: FormationContext): Lazy<Expression> = lazyOf(this@asStub)
}
