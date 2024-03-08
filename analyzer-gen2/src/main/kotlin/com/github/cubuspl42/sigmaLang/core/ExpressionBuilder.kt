package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

abstract class ExpressionBuilder<out T> {
    companion object {
        val projectReference: ExpressionBuilder<ProjectBuilder.Reference> =
            object : ExpressionBuilder<ProjectBuilder.Reference>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = buildContext.projectReference
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

        fun referBuiltin(
            name: Identifier,
        ): ExpressionBuilder<ShadowExpression> = object : ExpressionBuilder<Expression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Expression = buildContext.referBuiltin(name = name)
        }

        val ifFunction: ExpressionBuilder<ExpressionStub.IfFunction> =
            object : ExpressionBuilder<ExpressionStub.IfFunction>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = ExpressionStub.IfFunction(
                    callee = buildContext.referBuiltin(
                        name = Identifier(name = "if"),
                    ),
                )
            }

        val panicFunction = referBuiltin(
            name = Identifier(name = "panic"),
        )

        val isAFunction = referBuiltin(
            name = Identifier(name = "isA"),
        )

        val listClass = referBuiltin(
            name = Identifier(name = "List"),
        )
    }

    abstract fun build(
        buildContext: Expression.BuildContext,
    ): T

    fun asStub(): ExpressionStub<T> = ExpressionStub.pure(this)
}

fun <TExpression : ShadowExpression> ExpressionBuilder<TExpression>.buildRaw(
    buildContext: Expression.BuildContext,
): Expression = build(
    buildContext = buildContext,
).rawExpression

fun <TExpression, RExpression> ExpressionBuilder<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionBuilder<RExpression> = object : ExpressionBuilder<RExpression>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): RExpression = function(
        this@map.build(buildContext = buildContext),
    )
}

fun <TExpression1, TExpression2> ExpressionBuilder<TExpression1>.joinOf(
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
