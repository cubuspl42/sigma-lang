package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.stubs.AbstractionConstructorStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.LocalScopeStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ReferenceStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class ModuleTerm(
    val definitions: List<DefinitionTerm>,
) : Term {
    sealed class DefinitionTerm : Term {
        companion object {
            fun build(
                ctx: SigmaParser.ModuleDefinitionContext,
            ): DefinitionTerm = object : SigmaParserBaseVisitor<DefinitionTerm>() {
                override fun visitValueDefinition(
                    ctx: SigmaParser.ValueDefinitionContext,
                ): DefinitionTerm = ValueDefinitionTerm.build(ctx)

                override fun visitFunctionDefinition(
                    ctx: SigmaParser.FunctionDefinitionContext,
                ): DefinitionTerm = FunctionDefinitionTerm.build(ctx)

                override fun visitClassDefinition(
                    ctx: SigmaParser.ClassDefinitionContext,
                ): DefinitionTerm = ClassDefinitionTerm.build(ctx)
            }.visit(ctx)
        }

        fun transmute(): LocalScopeStub.DefinitionStub {
            return LocalScopeStub.DefinitionStub(
                key = name.transmute(),
                initializerStub = transmuteInitializer(),
            )
        }

        abstract val name: IdentifierTerm

        abstract fun transmuteInitializer(): ExpressionStub<*>
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

        override fun transmuteInitializer() = initializer.transmute()
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

        override fun transmuteInitializer() = AbstractionConstructorTerm(
            argumentType = argumentType,
            image = body,
        ).transmute()
    }

    companion object : Term.Builder<SigmaParser.ModuleContext, ModuleTerm>() {
        override fun build(
            ctx: SigmaParser.ModuleContext,
        ): ModuleTerm = ModuleTerm(
            definitions = ctx.moduleDefinition().map { DefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ModuleContext = parser.module()
    }

    fun transmute() = AbstractionConstructorStub(
        argumentNames = emptySet(), // FIXME?
        image = LocalScopeStub.of(
            definitions = definitions.mapUniquely {
                it.transmute()
            },
            result = ReferenceStub(
                referredName = Identifier(name = "main"),
            ),
        )
    )
}
