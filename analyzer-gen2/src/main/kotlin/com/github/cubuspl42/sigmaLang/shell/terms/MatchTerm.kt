package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.MatcherConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

data class MatchTerm(
    val matched: ExpressionTerm,
    val patternBlocks: List<CaseTerm>,
) : ExpressionTerm {
    data class CaseTerm(
        val pattern: PatternTerm,
        val result: ExpressionTerm,
    ) : Wrappable {
        companion object {
            fun build(
                ctx: SigmaParser.MatchCaseContext,
            ): CaseTerm = CaseTerm(
                pattern = object : SigmaParserBaseVisitor<PatternTerm>() {
                    override fun visitDestructuringPattern(
                        ctx: SigmaParser.DestructuringPatternContext,
                    ): PatternTerm = DestructuringPatternTerm.build(ctx)

                    override fun visitTagPattern(
                        ctx: SigmaParser.TagPatternContext,
                    ): PatternTerm = TagPatternTerm.build(ctx)
                }.visit(ctx.matchPattern()),
                result = ExpressionTerm.build(ctx.result),
            )
        }

        override fun wrap(): Value = TODO()
    }

    companion object : Term.Builder<SigmaParser.MatchContext, MatchTerm>() {
        override fun build(
            ctx: SigmaParser.MatchContext,
        ): MatchTerm = MatchTerm(
            matched = ExpressionTerm.build(ctx.matched),
            patternBlocks = ctx.patternBlocks.map {
                CaseTerm.build(it)
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.MatchContext = parser.match()
    }

    override fun transmute(context: TransmutationContext): Expression {
        val matchedExpression = matched.transmute(
            context = context,
        )

        val patternBlocks = patternBlocks.map { patternBlock ->
            val pattern = patternBlock.pattern
            object : MatcherConstructor.PatternBlock(
                pattern = pattern.transmute(
                    context = context,
                ),
            ) {
                override fun makeResult(
                    definitionBlock: LocalScope.DefinitionBlock,
                ): Expression {
                    val innerScope = object : StaticScope {
                        override fun resolveName(
                            referredName: Identifier,
                        ): Expression? = if (pattern.names.contains(referredName)) {
                            definitionBlock.getInitializer(name = referredName)
                        } else null
                    }.chainWith(
                        context.scope,
                    )

                    return patternBlock.result.transmute(
                        context = context.copy(
                            scope = innerScope,
                        ),
                    )
                }
            }
        }

        return MatcherConstructor.make(
            matched = matchedExpression,
            patternBlocks = patternBlocks,
            elseResult = BuiltinModuleReference.panicFunction.call(),
        ).rawExpression
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("matched") to lazyOf(matched.wrap()),
            Identifier.of("patternBlocks") to lazyOf(patternBlocks.wrap()),
        )
    )
}
