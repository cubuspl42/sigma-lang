package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

class AbstractionConstructorStub(
    private val argumentNames: Set<Identifier>,
    private val buildBody: (ArgumentReference) -> ExpressionStub<Expression>,
) : ExpressionStub<AbstractionConstructor>() {
    companion object {
        fun of(
            argumentNames: Set<Identifier>,
            buildBody: (ArgumentReference) -> ExpressionStub<Expression>,
        ): AbstractionConstructorStub = AbstractionConstructorStub(
            argumentNames = argumentNames,
            buildBody = buildBody,
        )

        fun of(
            argumentNames: Set<Identifier>,
            body: ExpressionStub<Expression>,
        ): AbstractionConstructorStub = AbstractionConstructorStub(
            argumentNames = argumentNames,
            buildBody = { body },
        )
    }

    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<AbstractionConstructor> = AbstractionConstructor.builder { argumentReference ->
        val innerScope = StaticScope.argumentScope(
            argumentNames = argumentNames,
            argumentReference = argumentReference,
        ).chainWith(
            context.scope,
        )

        val innerContext = context.copy(
            scope = innerScope,
        )

        buildBody(
            argumentReference,
        ).transform(
            context = innerContext,
        )
    }

    fun withArgumentName(name: Identifier) = AbstractionConstructorStub(
        argumentNames = argumentNames + name,
        buildBody = buildBody,
    )
}
