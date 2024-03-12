package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

abstract class ExpressionStub<out T> {
    companion object {
        fun <TExpression> pure(
            builder: ExpressionBuilder<TExpression>,
        ): ExpressionStub<TExpression> = object : ExpressionStub<TExpression>() {
            override fun transform(
                context: FormationContext,
            ): ExpressionBuilder<TExpression> = builder
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2Unpacked(
            stub1: ExpressionStub<TExpression1>,
            stub2: ExpressionStub<TExpression2>,
            function: (TExpression1, TExpression2) -> ExpressionBuilder<TExpression3>,
        ): ExpressionStub<TExpression3> = object : ExpressionStub<TExpression3>() {
            override fun transform(
                context: FormationContext,
            ): ExpressionBuilder<TExpression3> = ExpressionBuilder.map2Joined(
                stub1.transform(context = context),
                stub2.transform(context = context),
                function = function,
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression, TExpression4 : ShadowExpression> map3Unpacked(
            stub1: ExpressionStub<TExpression1>,
            stub2: ExpressionStub<TExpression2>,
            stub3: ExpressionStub<TExpression3>,
            function: (TExpression1, TExpression2, TExpression3) -> ExpressionBuilder<TExpression4>,
        ): ExpressionStub<TExpression4> = object : ExpressionStub<TExpression4>() {
            override fun transform(
                context: FormationContext,
            ): ExpressionBuilder<TExpression4> = ExpressionBuilder.map3Joined(
                stub1.transform(context = context),
                stub2.transform(context = context),
                stub3.transform(context = context),
                function = function,
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2Nested(
            stub1: ExpressionStub<TExpression1>,
            stub2: ExpressionStub<TExpression2>,
            function: (TExpression1, TExpression2) -> TExpression3,
        ): ExpressionStub<TExpression3> = object : ExpressionStub<TExpression3>() {
            override fun transform(
                context: FormationContext,
            ): ExpressionBuilder<TExpression3> = ExpressionBuilder.map2(
                builder1 = stub1.transform(context = context),
                builder2 = stub2.transform(context = context),
                function = function,
            )
        }
    }

    abstract fun transform(
        context: FormationContext,
    ): ExpressionBuilder<T>

    fun build(
        formationContext: FormationContext,
        buildContext: Expression.BuildContext,
    ): T = transform(
        context = formationContext,
    ).build(
        buildContext = buildContext,
    )
}

fun <TExpression : Any, RExpression: Any> ExpressionStub<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionStub<RExpression> = object : ExpressionStub<RExpression>() {
    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<RExpression> = this@map.transform(
        context = context,
    ).map(function)
}

fun <TExpression : ShadowExpression> TExpression.asStub(): ExpressionStub<TExpression> =
    object : ExpressionStub<TExpression>() {
        override fun transform(context: FormationContext): ExpressionBuilder<TExpression> =
            ExpressionBuilder.pure(this@asStub)
    }
