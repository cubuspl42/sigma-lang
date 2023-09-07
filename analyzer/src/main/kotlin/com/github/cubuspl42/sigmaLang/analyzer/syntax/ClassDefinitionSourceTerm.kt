package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ClassDefinitionContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Constness
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

data class ClassDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val body: UnorderedTupleTypeConstructorTerm,
) : NamespaceEntrySourceTerm(), ClassDefinitionTerm {
    companion object {
        fun build(
            ctx: ClassDefinitionContext,
        ): ClassDefinitionSourceTerm = ClassDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            body = UnorderedTupleTypeConstructorSourceTerm.build(
                location = SourceLocation.build(ctx.body),
                constness = Constness.Variable,
                ctx = ctx.body,
            ),
        )
    }
}
