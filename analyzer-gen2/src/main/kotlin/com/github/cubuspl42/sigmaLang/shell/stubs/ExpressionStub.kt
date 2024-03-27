package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.FormationContext

abstract class ExpressionStub<out T> {
    companion object {
        fun <TExpression> pure(
            value: TExpression,
        ): ExpressionStub<TExpression> = object : ExpressionStub<TExpression>() {
            override fun transform(
                context: FormationContext,
            ): TExpression = value
        }

        fun <TExpression1 : Expression, TExpression2 : Expression, TExpression3 : Expression, TExpression4 : Expression> map3(
            stub1: ExpressionStub<TExpression1>,
            stub2: ExpressionStub<TExpression2>,
            stub3: ExpressionStub<TExpression3>,
            function: (TExpression1, TExpression2, TExpression3) -> TExpression4,
        ): ExpressionStub<TExpression4> = object : ExpressionStub<TExpression4>() {
            override fun transform(
                context: FormationContext,
            ): TExpression4 = function(
                stub1.transform(context = context),
                stub2.transform(context = context),
                stub3.transform(context = context),
            )
        }

        fun <TExpression1 : Expression, TExpression2 : Expression, TExpression3 : Expression> map2Nested(
            stub1: ExpressionStub<TExpression1>,
            stub2: ExpressionStub<TExpression2>,
            function: (TExpression1, TExpression2) -> TExpression3,
        ): ExpressionStub<TExpression3> = object : ExpressionStub<TExpression3>() {
            override fun transform(
                context: FormationContext,
            ): TExpression3 = function(
                stub1.transform(context = context),
                stub2.transform(context = context),
            )
        }
    }

    abstract fun transform(
        context: FormationContext,
    ): T

    fun build(
        formationContext: FormationContext,
    ): T = transform(
        context = formationContext,
    )
}

fun <TExpression : Any, RExpression : Any> ExpressionStub<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionStub<RExpression> = object : ExpressionStub<RExpression>() {
    override fun transform(
        context: FormationContext,
    ): RExpression = function(
        this@map.transform(
            context = context,
        ),
    )
}
