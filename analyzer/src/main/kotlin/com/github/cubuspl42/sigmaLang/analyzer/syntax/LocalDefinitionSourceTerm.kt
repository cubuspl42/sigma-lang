package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.DefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class LocalDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionTerm? = null,
    override val body: ExpressionTerm,
) : SourceTerm(), LocalDefinitionTerm {
    companion object {
        fun build(
            ctx: DefinitionContext,
        ): LocalDefinitionSourceTerm = LocalDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.valueType?.let { ExpressionSourceTerm.build(it) },
            body = ExpressionSourceTerm.build(ctx.value),
        )
    }
}
