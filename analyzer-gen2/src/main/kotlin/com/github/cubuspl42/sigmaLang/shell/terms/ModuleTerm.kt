package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ExpressionScope
import com.github.cubuspl42.sigmaLang.shell.scope.FieldScope
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

        fun transmute(): LocalScopeStub.DefinitionStub = LocalScopeStub.DefinitionStub(
            key = name.transmute(),
            initializerStub = transmuteInitializer(),
        )

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
        val builtinIdentifier = Identifier(name = "builtin")

        override fun build(
            ctx: SigmaParser.ModuleContext,
        ): ModuleTerm = ModuleTerm(
            definitions = ctx.moduleDefinition().map { DefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ModuleContext = parser.module()
    }

    fun build() = AbstractionConstructor.looped { argumentReference ->
        LocalScopeStub.of(
            definitions = definitions.mapUniquely {
                it.transmute()
            },
        ).transform(
            context = FormationContext(
                scope = ExpressionScope(
                    name = builtinIdentifier,
                    boundExpression = argumentReference.readField(
                        builtinIdentifier,
                    ),
                ),
            )
        ).build(
            buildContext = Expression.BuildContext(
                builtin = argumentReference.readField(
                    fieldName = builtinIdentifier,
                )
            )
        ).readField(
            fieldName = Identifier(name = "main"),
        )
    }
}
