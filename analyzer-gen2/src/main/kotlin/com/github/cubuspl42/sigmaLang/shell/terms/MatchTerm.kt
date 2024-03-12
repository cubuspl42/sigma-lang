package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.MatcherConstructor
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.TagPattern
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class MatchTerm(
    val matched: ExpressionTerm,
    val patternBlocks: List<PatternBlock>,
) : ExpressionTerm {
    data class PatternBlock(
        @Suppress("PropertyName") val class_: ExpressionTerm,
        val newName: Identifier,
        val result: ExpressionTerm,
    ) : Wrappable {
        override fun wrap(): Value = UnorderedTuple(
            valueByKey = mapOf(
                Identifier.of("class") to lazyOf(class_.wrap()),
                Identifier.of("result") to lazyOf(result.wrap()),
            ),
        )
    }

    companion object : Term.Builder<SigmaParser.MatchContext, MatchTerm>() {
        override fun build(
            ctx: SigmaParser.MatchContext,
        ): MatchTerm = MatchTerm(
            matched = ExpressionTerm.build(ctx.matched),
            patternBlocks = ctx.patternBlocks.map {
                PatternBlock(
                    class_ = ExpressionTerm.build(it.class_),
                    newName = IdentifierTerm.build(it.newName).transmute(),
                    result = ExpressionTerm.build(it.result),
                )
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.MatchContext = parser.match()
    }

    override fun transmute() = object : ExpressionStub<ShadowExpression>() {
        override fun transform(context: FormationContext): ExpressionBuilder<ShadowExpression> =
            object : ExpressionBuilder<ShadowExpression>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = MatcherConstructor.make(
                    matched = matched.build(
                        formationContext = context,
                        buildContext = buildContext,
                    ),
                    patternBlocks = patternBlocks.map { patternBlock ->
                        object : MatcherConstructor.PatternBlock(
                            pattern = TagPattern(
                                builtinModuleReference = buildContext.builtinModule,
                                newName = patternBlock.newName,
                                class_ = patternBlock.class_.build(
                                    formationContext = context,
                                    buildContext = buildContext,
                                ),
                            ),
                        ) {
                            override fun makeResult(
                                definitionBlock: LocalScope.DefinitionBlock,
                            ): ShadowExpression {
                                val innerScope = object : StaticScope {
                                    override fun resolveName(
                                        referredName: Identifier,
                                    ): Expression? = if (referredName == patternBlock.newName) {
                                        definitionBlock.getInitializer(name = referredName).rawExpression
                                    } else null
                                }.chainWith(
                                    context.scope,
                                )

                                return patternBlock.result.build(
                                    formationContext = context.copy(
                                        scope = innerScope,
                                    ),
                                    buildContext = buildContext,
                                )
                            }
                        }
                    },
                    elseResult = ExpressionBuilder.panicCall.build(
                        buildContext = buildContext,
                    ),
                ).build(
                    buildContext = buildContext,
                )
            }
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("matched") to lazyOf(matched.wrap()),
            Identifier.of("patternBlocks") to lazyOf(patternBlocks.wrap()),
        )
    )
}
