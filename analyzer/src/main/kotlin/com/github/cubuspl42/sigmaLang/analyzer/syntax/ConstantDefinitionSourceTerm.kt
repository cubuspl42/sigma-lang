package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ConstantDefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class ConstantDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionTerm? = null,
    override val body: ExpressionTerm,
) : NamespaceEntrySourceTerm(), ConstantDefinitionTerm {
    companion object {
        fun build(
            ctx: ConstantDefinitionContext,
        ): ConstantDefinitionSourceTerm = ConstantDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.type?.let { ExpressionSourceTerm.build(it) },
            body = ExpressionSourceTerm.build(ctx.definer),
        )
    }
}
