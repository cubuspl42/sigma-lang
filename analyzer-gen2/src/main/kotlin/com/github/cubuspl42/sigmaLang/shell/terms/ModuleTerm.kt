package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext

data class ModuleTerm(
    val definitions: List<DefinitionTerm>,
): Term {
    sealed class DefinitionTerm {
        companion object {
            fun build(
                ctx: SigmaParser.DefinitionContext,
            ): DefinitionTerm = object : SigmaParserBaseVisitor<DefinitionTerm>() {
                override fun visitValueDefinition(
                    ctx: SigmaParser.ValueDefinitionContext,
                ): DefinitionTerm = ValueDefinitionTerm.build(ctx)

                override fun visitFunctionDefinition(
                    ctx: SigmaParser.FunctionDefinitionContext,
                ): DefinitionTerm = FunctionDefinitionTerm.build(ctx)
            }.visit(ctx)
        }

        val asEntry: UnorderedTupleConstructorTerm.Entry
            get() = UnorderedTupleConstructorTerm.Entry(
                key = name,
                value = effectiveInitializer,
            )

        abstract val name: IdentifierTerm

        abstract val effectiveInitializer: ExpressionTerm
    }

    data class ValueDefinitionTerm(
        override val name: IdentifierTerm,
        val initializer: ExpressionTerm,
    ) : DefinitionTerm() {
        companion object {
            fun build(
                ctx: SigmaParser.ValueDefinitionContext,
            ): ValueDefinitionTerm = ValueDefinitionTerm(
                name = IdentifierTerm.build(ctx.name),
                initializer = ExpressionTerm.build(ctx.initializer),
            )
        }

        override val effectiveInitializer: ExpressionTerm
            get() = initializer
    }

    data class FunctionDefinitionTerm(
        override val name: IdentifierTerm,
        val argumentType: UnorderedTupleTypeConstructorTerm,
        val body: ExpressionTerm,
    ) : DefinitionTerm() {
        companion object {
            fun build(
                ctx: SigmaParser.FunctionDefinitionContext,
            ): FunctionDefinitionTerm = FunctionDefinitionTerm(
                name = IdentifierTerm.build(ctx.name),
                argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
                body = ExpressionTerm.build(ctx.body),
            )
        }

        override val effectiveInitializer: AbstractionConstructorTerm
            get() = AbstractionConstructorTerm(
                argumentType = argumentType,
                image = body,
            )
    }

    companion object : Term.Builder<SigmaParser.ModuleContext, ModuleTerm>() {
        override fun build(
            ctx: SigmaParser.ModuleContext,
        ): ModuleTerm = ModuleTerm(
            definitions = ctx.definition().map { DefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ModuleContext = parser.module()
    }

    fun construct(
        context: ConstructionContext,
    ): Lazy<AbstractionConstructor> = AbstractionConstructorTerm(
        argumentType = UnorderedTupleTypeConstructorTerm.Empty,
        image = LetInTerm(
            block = UnorderedTupleConstructorTerm(
                entries = definitions.map { it.asEntry },
            ),
            result = ReferenceTerm(
                referredName = IdentifierTerm(name = "main"),
            ),
        ),
    ).construct(
        context = context,
    )
}
