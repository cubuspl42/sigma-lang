package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.MetaDefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class MetaDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Identifier,
    override val declaredTypeBody: ExpressionTerm? = null,
    override val body: ExpressionTerm,
) : NamespaceEntrySourceTerm(), MetaDefinitionTerm {
    companion object {
        fun build(
            ctx: MetaDefinitionContext,
        ): MetaDefinitionSourceTerm = MetaDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Identifier.of(ctx.name.text),
            declaredTypeBody = ctx.type?.let { ExpressionSourceTerm.build(it) },
            body = ExpressionSourceTerm.build(ctx.definer),
        )
    }
}
