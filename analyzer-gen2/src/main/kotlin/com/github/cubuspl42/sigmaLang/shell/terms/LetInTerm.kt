package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class LetInTerm(
    val definitions: Set<DefinitionTerm>,
    val result: ExpressionTerm,
) : ExpressionTerm {
    data class DefinitionTerm(
        val pattern: PatternTerm,
        val initializer: ExpressionTerm,
    ) {
        val names: Set<Identifier>
            get() = pattern.names

        companion object {
            fun build(
                ctx: SigmaParser.LetInBlockEntryContext,
            ): DefinitionTerm = DefinitionTerm(
                pattern = PatternTerm.build(ctx.pattern()),
                initializer = ExpressionTerm.build(ctx.initializer),
            )
        }
    }

    companion object : Term.Builder<SigmaParser.LetInContext, LetInTerm>() {
        override fun build(
            ctx: SigmaParser.LetInContext,
        ): LetInTerm = LetInTerm(
            definitions = ctx.letInBlock().letInBlockEntry().mapUniquely {
                DefinitionTerm.build(it)
            },
            result = ExpressionTerm.build(ctx.expression()),
        )

        override fun extract(parser: SigmaParser): SigmaParser.LetInContext = parser.letIn()
    }

    override fun transmute() = object : ExpressionStub<ShadowExpression>() {
        override fun transform(
            context: FormationContext,
        ) = object : ExpressionBuilder<ShadowExpression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): ShadowExpression {
                val allLocalNames = definitions.fold(
                    initial = emptySet<Identifier>()
                ) { accLocalNames, definitionTerm ->
                    accLocalNames + definitionTerm.names
                }

                fun <T : Any> withExtendedScope(
                    localScopeReference: LocalScope.Reference,
                    block: (innerContext: FormationContext) -> T,
                ): T {
                    val innerScope = object : StaticScope {
                        override fun resolveName(
                            referredName: Identifier,
                        ): Expression? = if (referredName in allLocalNames) {
                            localScopeReference.referDefinitionInitializer(
                                name = referredName,
                            ).rawExpression
                        } else null
                    }.chainWith(
                        context.scope,
                    )

                    val innerContext = context.copy(
                        scope = innerScope,
                    )

                    return block(innerContext)
                }

                val result = LocalScope.Constructor.makeWithResult(
                    makeDefinitions = { localScopeReference ->
                        withExtendedScope(
                            localScopeReference = localScopeReference,
                        ) { innerContext ->
                            definitions.mapUniquely {
                                it.pattern.transmute(
                                    initializerStub = it.initializer.transmute(),
                                ).build(
                                    formationContext = innerContext,
                                    buildContext = buildContext,
                                )
                            }
                        }
                    },
                    makeResult = { localScopeReference ->
                        withExtendedScope(
                            localScopeReference = localScopeReference,
                        ) { innerContext ->
                            result.transmute().build(
                                formationContext = innerContext,
                                buildContext = buildContext,
                            )
                        }
                    },
                ).build(
                    buildContext = buildContext,
                )

                return result
            }
        }

    }

    override fun wrap(): Value = TODO()
}
