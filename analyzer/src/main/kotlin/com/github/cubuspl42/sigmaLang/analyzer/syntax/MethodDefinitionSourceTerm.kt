package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.MethodDefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class MethodDefinitionSourceTerm(
    override val location: SourceLocation,
    override val thisType: ExpressionTerm,
    override val name: Identifier,
    override val body: AbstractionConstructorTerm,
) : NamespaceEntrySourceTerm(), MethodDefinitionTerm {
    companion object {
        fun build(
            ctx: MethodDefinitionContext,
        ): MethodDefinitionSourceTerm = MethodDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            thisType = ExpressionSourceTerm.build(ctx.thisType),
            name = Identifier.of(ctx.name.text),
            body = AbstractionConstructorSourceTerm.build(ctx.body),
        )
    }
}
