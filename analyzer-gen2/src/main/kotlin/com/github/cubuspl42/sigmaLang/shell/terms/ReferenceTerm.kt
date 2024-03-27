package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class ReferenceTerm(
    val referredName: IdentifierTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.ReferenceContext, ReferenceTerm>() {
        override fun build(
            ctx: SigmaParser.ReferenceContext,
        ): ReferenceTerm = ReferenceTerm(
            referredName = IdentifierTerm.build(ctx.referredName),
        )

        override fun extract(parser: SigmaParser): SigmaParser.ReferenceContext = parser.reference()
    }

    override fun transmute(): ExpressionStub<Expression> = object : ExpressionStub<Expression>() {
        override fun transform(
            context: FormationContext,
        ): Expression {
            val scope = context.scope

            val referredExpression = scope.resolveName(referredName = referredName.toIdentifier())
                ?: throw IllegalStateException("Unresolved reference: $referredName")

            return referredExpression
        }
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("referredName") to lazyOf(referredName.wrap()),
        ),
    )
}
