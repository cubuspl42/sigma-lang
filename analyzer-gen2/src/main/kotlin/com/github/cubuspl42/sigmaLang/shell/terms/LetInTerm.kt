package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.map
import com.github.cubuspl42.sigmaLang.shell.withExtendedScope
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class LetInTerm(
    val definitions: Set<DefinitionTerm>,
    val result: ExpressionTerm,
) : ExpressionTerm {
    data class DefinitionTerm(
        val lhs: LhsTerm,
        val initializer: ExpressionTerm,
    ) {
        constructor(
            name: IdentifierTerm,
            initializer: ExpressionTerm,
        ) : this(
            lhs = NameLhsTerm(name = name.transmute()),
            initializer = initializer,
        )

        sealed class LhsTerm {
            companion object {
                fun build(
                    ctx: SigmaParser.DefinitionLhsContext,
                ): LhsTerm = object : SigmaParserBaseVisitor<LhsTerm>() {
                    override fun visitDestructuringPatternDefinitionLhs(
                        ctx: SigmaParser.DestructuringPatternDefinitionLhsContext,
                    ) = DestructuringLhsTerm.build(
                        ctx = ctx.destructuringPattern(),
                    )

                    override fun visitNameDefinitionLhs(
                        ctx: SigmaParser.NameDefinitionLhsContext,
                    ) = NameLhsTerm.build(
                        ctx = ctx,
                    )
                }.visit(ctx)
            }

            abstract val names: Set<Identifier>

            abstract fun makeDefinition(
                initializer: ExpressionTerm,
            ): ExpressionStub<LocalScope.Constructor.Definition>
        }

        data class NameLhsTerm(
            val name: Identifier,
        ) : LhsTerm() {
            companion object {
                fun build(
                    ctx: SigmaParser.NameDefinitionLhsContext,
                ): NameLhsTerm = NameLhsTerm(
                    name = IdentifierTerm.build(ctx.name).transmute(),
                )
            }

            override val names: Set<Identifier>
                get() = setOf(name)

            override fun makeDefinition(
                initializer: ExpressionTerm,
            ) = initializer.transmute().map {
                LocalScope.Constructor.SimpleDefinition(
                    name = name,
                    initializer = it,
                )
            }
        }

        data class DestructuringLhsTerm(
            val pattern: DestructuringPatternTerm,
        ) : LhsTerm() {
            companion object {
                fun build(
                    ctx: SigmaParser.DestructuringPatternContext,
                ): DestructuringLhsTerm = DestructuringLhsTerm(
                    pattern = DestructuringPatternTerm.build(ctx),
                )
            }

            override val names: Set<Identifier>
                get() = pattern.names

            override fun makeDefinition(
                initializer: ExpressionTerm,
            ) = object : ExpressionStub<LocalScope.Constructor.PatternDefinition>() {
                override fun transform(
                    context: FormationContext,
                ) = object : ExpressionBuilder<LocalScope.Constructor.PatternDefinition>() {
                    override fun build(
                        buildContext: Expression.BuildContext,
                    ): LocalScope.Constructor.PatternDefinition {
                        val pattern = pattern.makePattern().build(
                            formationContext = context,
                            buildContext = buildContext,
                        )

                        return LocalScope.Constructor.PatternDefinition(
                            builtinModuleReference = buildContext.builtinModule,
                            pattern = pattern,
                            initializer = initializer.transmute().build(
                                formationContext = context,
                                buildContext = buildContext,
                            ),
                        )
                    }
                }
            }
        }

        companion object {
            fun build(
                ctx: SigmaParser.DefinitionContext,
            ): DefinitionTerm = DefinitionTerm(
                lhs = LhsTerm.build(ctx.definitionLhs()),
                initializer = ExpressionTerm.build(ctx.initializer),
            )
        }

        val names: Set<Identifier>
            get() = lhs.names

        fun makeDefinition() = lhs.makeDefinition(initializer = initializer)
    }

//    data class SimpleDefinitionTerm(
//        val name: Identifier,
//        override val initializer: ExpressionTerm,
//    ) : DefinitionTerm() {
//        companion object {
//            fun build(
//                ctx: SigmaParser.NameDefinitionLhsContext,
//                initializer: ExpressionTerm,
//            ): SimpleDefinitionTerm = SimpleDefinitionTerm(
//                name = IdentifierTerm.build(ctx.name).transmute(),
//                initializer = initializer,
//            )
//        }
//    }
//
//    data class DestructuringDefinitionTerm(
//        val pattern: DestructuringPatternTerm,
//        override val initializer: ExpressionTerm,
//    ) : DefinitionTerm() {
//        val names: Set<Identifier>
//            get() = pattern.names
//
//        companion object {
//            fun build(
//                ctx: SigmaParser.DestructuringPatternContext,
//                initializer: ExpressionTerm,
//            ): DestructuringDefinitionTerm = DestructuringDefinitionTerm(
//                pattern = DestructuringPatternTerm.build(ctx.pattern()),
//                initializer = initializer,
//            )
//        }
//    }

    companion object : Term.Builder<SigmaParser.LetInContext, LetInTerm>() {
        override fun build(
            ctx: SigmaParser.LetInContext,
        ): LetInTerm = LetInTerm(
            definitions = ctx.definitionBlock().definition().mapUniquely {
                DefinitionTerm.build(it)
            },
            result = ExpressionTerm.build(ctx.expression()),
        )

        override fun extract(parser: SigmaParser): SigmaParser.LetInContext = parser.letIn()
    }

    override fun transmute() = object : ExpressionStub<Expression>() {
        override fun transform(
            context: FormationContext,
        ) = object : ExpressionBuilder<Expression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Expression {
                val allLocalNames = definitions.fold(
                    initial = emptySet<Identifier>()
                ) { accLocalNames, definitionTerm ->
                    accLocalNames + definitionTerm.names
                }


                val result = LocalScope.Constructor.makeWithResult(
                    makeDefinitions = { localScopeReference ->
                        val innerContext = context.withExtendedScope(
                            localNames = allLocalNames,
                            localScopeReference = localScopeReference,
                        )

                        definitions.mapUniquely {
                            it.makeDefinition().build(
                                formationContext = innerContext,
                                buildContext = buildContext,
                            )
                        }
                    },
                    makeResult = { localScopeReference ->
                        val innerContext = context.withExtendedScope(
                            localNames = allLocalNames,
                            localScopeReference = localScopeReference,
                        )

                        result.transmute().build(
                            formationContext = innerContext,
                            buildContext = buildContext,
                        )
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
