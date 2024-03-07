package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ModuleBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.joinOf
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ExpressionScope
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.LocalScopeStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

// module
//    : imports=import_*
//    | moduleDefinition+
//    ;

data class ModuleTerm(
    val imports: List<ImportTerm>,
    val definitions: List<DefinitionTerm>,
) : Term {
    data class ImportTerm(
        val importedModuleName: IdentifierTerm,
    ) : Term {
        companion object {
            fun build(
                ctx: SigmaParser.Import_Context,
            ): ImportTerm = ImportTerm(
                importedModuleName = IdentifierTerm.build(ctx.importedModuleName),
            )
        }
    }

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
        override fun build(
            ctx: SigmaParser.ModuleContext,
        ): ModuleTerm = ModuleTerm(
            imports = ctx.import_().map { ImportTerm.build(it) },
            definitions = ctx.moduleDefinition().map { DefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ModuleContext = parser.module()
    }

    fun build(): ModuleBuilder.Constructor {
        val memberNames = definitions.mapUniquely { it.name.transmute() }

        val moduleBuilder = ModuleBuilder(
            memberDefinitionBuilders = definitions.mapUniquely { definitionTerm ->
                val definitionStub = definitionTerm.transmute()

                object : ModuleBuilder.MemberDefinitionBuilder(
                    name = definitionTerm.name.transmute(),
                ) {
                    override fun buildInitializer(
                        moduleReference: ModuleBuilder.Reference,
                    ) = ExpressionBuilder.builtin.joinOf { builtin ->
                        val rootScope = object : StaticScope {
                            override fun resolveName(
                                referredName: Identifier,
                            ): Expression? = if (memberNames.contains(referredName)) {
                                moduleReference.referDefinition(
                                    referredDefinitionName = referredName,
                                )
                            } else null
                        }.chainWith(
                            ExpressionScope(
                                name = Identifier(name = "builtin"),
                                boundExpression = builtin,
                            )
                        )

                        return@joinOf definitionStub.initializerStub.transform(
                            context = FormationContext(
                                scope = rootScope,
                            ),
                        )
                    }
                }
            },
        )

        return moduleBuilder.build()
    }
}
