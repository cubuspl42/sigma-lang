package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.concepts.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

// TODO: Nuke?
class ReferenceStub(
    private val referredName: Identifier,
) : ExpressionStub<Expression>() {

    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<Expression> {
        val scope = context.scope

        val referredExpression = scope.resolveName(referredName = referredName)
            ?: throw IllegalStateException("Unresolved reference: $referredName")

        return ExpressionBuilder.pure(referredExpression)
    }
}
