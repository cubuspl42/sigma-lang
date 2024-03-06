package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

abstract class ExpressionStub<TExpression : Expression> {
    data class IfFunctionStub(
        val calleeStub: ExpressionStub<*>,
    ) {
        fun call(
            condition: ExpressionStub<*>,
            thenCase: ExpressionStub<*>,
            elseCase: ExpressionStub<*>,
        ): ExpressionStub<*> = CallStub(
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
        fun looped(
            block: (Lazy<Expression>) -> ExpressionStub<*>,
        ): ExpressionStub<*> = object : ExpressionStub<Expression>() {
            override fun form(context: FormationContext): Lazy<Expression> = lazy {
                LazyUtils.looped { expressionLooped ->
                    block(expressionLooped).form(
                        context = context,
                    ).value
                }
            }
        }

        fun referBuiltin(
            name: Identifier,
        ): ExpressionStub<*> = object : ExpressionStub<Expression>() {
            override fun form(context: FormationContext) = CallStub.fieldRead(
                subjectStub = CallStub.fieldRead(
                    subjectStub = ArgumentReference(
                        referredAbstractionLazy = context.moduleRoot,
                    ).asStub(),

                    fieldName = Identifier(
                        name = "builtin",
                    ),
                ),
                fieldName = name,
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
    ): Lazy<TExpression>

    fun formStrict(
        context: FormationContext,
    ): TExpression = form(context = context).value
}

fun <TExpression : Expression, RExpression : Expression> ExpressionStub<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionStub<RExpression> = object : ExpressionStub<RExpression>() {
    override fun form(context: FormationContext): Lazy<RExpression> = lazyOf(
        function(
            this@map.formStrict(
                context = context,
            ),
        ),
    )
}

fun <TExpression : Expression> TExpression.asStub(): ExpressionStub<TExpression> =
    object : ExpressionStub<TExpression>() {
        override fun form(
            context: FormationContext,
        ) = lazyOf(this@asStub)
    }
