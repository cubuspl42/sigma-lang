package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ClassDefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

data class ClassDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Identifier,
    override val body: UnorderedTupleTypeConstructorTerm,
) : NamespaceEntrySourceTerm(), ClassDefinitionTerm {
    companion object {
        fun build(
            ctx: ClassDefinitionContext,
        ): ClassDefinitionSourceTerm = ClassDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Identifier.of(ctx.name.text),
            body = UnorderedTupleTypeConstructorSourceTerm.build(ctx.body)
        )
    }

    override val declaredTypeBody: ExpressionTerm? = null
}
