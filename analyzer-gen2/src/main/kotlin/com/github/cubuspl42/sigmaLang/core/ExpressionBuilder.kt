package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
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

        fun <TExpression> pure(
            expression: TExpression,
        ): ExpressionBuilder<TExpression> = object : ExpressionBuilder<TExpression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression = expression
        }

        fun <TExpression1, TExpression2, TExpression3> map2(
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

        fun <TExpression1, TExpression2, TExpression3> map2Joined(
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

        fun <TExpression1, TExpression2, TExpression3, TExpression4> map3Joined(
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

        val ifFunction: ExpressionBuilder<BuiltinModuleReference.IfFunction> =
            object : ExpressionBuilder<BuiltinModuleReference.IfFunction>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = buildContext.builtinModule.ifFunction
            }

        val panicFunction: ExpressionBuilder<BuiltinModuleReference.PanicFunction> =
            object : ExpressionBuilder<BuiltinModuleReference.PanicFunction>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = buildContext.builtinModule.panicFunction
            }

        val panicCall = panicFunction.map {
            it.call()
        }

        val isAFunction = object : ExpressionBuilder<BuiltinModuleReference.IsAFunction>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ) = buildContext.builtinModule.isAFunction
        }
    }

    abstract fun build(
        buildContext: Expression.BuildContext,
    ): T

    fun asStub(): ExpressionStub<T> = ExpressionStub.pure(this)
}

fun <TExpression : Expression> ExpressionBuilder<TExpression>.buildRaw(
    buildContext: Expression.BuildContext,
): Expression = build(
    buildContext = buildContext,
)

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
