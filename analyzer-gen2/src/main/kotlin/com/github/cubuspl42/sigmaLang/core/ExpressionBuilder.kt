package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

abstract class ExpressionBuilder<out TExpression : ShadowExpression> {
    companion object {
        val builtin = object : ExpressionBuilder<Expression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Expression = buildContext.builtin
        }

        fun <TExpression : ShadowExpression> pure(
            expression: TExpression,
        ): ExpressionBuilder<TExpression> = object : ExpressionBuilder<TExpression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression = expression
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            function: (TExpression1, TExpression2) -> TExpression3,
        ): ExpressionBuilder<TExpression3> = object : ExpressionBuilder<TExpression3>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression3 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2Joined(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            function: (TExpression1, TExpression2) -> ExpressionBuilder<TExpression3>,
        ): ExpressionBuilder<TExpression3> = object : ExpressionBuilder<TExpression3>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression3 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
            ).build(
                buildContext = buildContext,
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression, TExpression4 : ShadowExpression> map3Joined(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            builder3: ExpressionBuilder<TExpression3>,
            function: (TExpression1, TExpression2, TExpression3) -> ExpressionBuilder<TExpression4>,
        ): ExpressionBuilder<TExpression4> = object : ExpressionBuilder<TExpression4>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression4 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
                builder3.build(buildContext = buildContext),
            ).build(
                buildContext = buildContext,
            )
        }
    }

    abstract fun build(
        buildContext: Expression.BuildContext,
    ): TExpression

    fun buildRaw(
        buildContext: Expression.BuildContext,
    ): Expression = build(
        buildContext = buildContext,
    ).rawExpression

    fun asStub(): ExpressionStub<TExpression> = ExpressionStub.pure(this)
}

fun <TExpression : ShadowExpression, RExpression : ShadowExpression> ExpressionBuilder<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionBuilder<RExpression> = object : ExpressionBuilder<RExpression>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): RExpression = function(
        this@map.build(buildContext = buildContext),
    )
}

fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression> ExpressionBuilder<TExpression1>.joinOf(
    extract: (TExpression1) -> ExpressionBuilder<TExpression2>,
): ExpressionBuilder<TExpression2> = object : ExpressionBuilder<TExpression2>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): TExpression2 {
        val expression1 = this@joinOf.build(buildContext = buildContext)
        val builder2 = extract(expression1)
        val expression2 = builder2.build(buildContext = buildContext)

        return expression2
    }
}
