package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.joinOf
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.withExtendedScope
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class ModuleTerm(
    val imports: List<ImportTerm>,
    val definitions: List<DefinitionTerm>,
) : Term {
    data class ImportTerm(
        val importedModuleName: IdentifierTerm,
        val aliasName: IdentifierTerm? = null,
    ) : Term {
        val importedModulePath: ModulePath
            get() = ModulePath(
                name = importedModuleName.transmute(),
            )

        companion object {
            fun build(
                ctx: SigmaParser.Import_Context,
            ): ImportTerm = ImportTerm(
                importedModuleName = IdentifierTerm.build(ctx.importedModuleName),
                aliasName = ctx.aliasName?.let { IdentifierTerm.build(it) },
            )
        }

        val effectiveName: IdentifierTerm
            get() = aliasName ?: importedModuleName

        override fun wrap(): Value = UnorderedTupleValue(
            valueByKey = mapOf(
                Identifier.of("importedModuleName") to lazyOf(importedModuleName.wrap()),
            )
        )
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

        abstract val name: Identifier

        abstract fun transmuteInitializer(): ExpressionStub<Expression>
    }

    data class ValueDefinitionTerm(
        override val name: Identifier,
        val initializer: ExpressionTerm,
    ) : DefinitionTerm() {
        companion object {
            fun build(
                ctx: SigmaParser.ValueDefinitionContext,
            ): ValueDefinitionTerm = ValueDefinitionTerm(
                name = IdentifierTerm.build(ctx.name).toIdentifier(),
                initializer = ExpressionTerm.build(ctx.initializer),
            )
        }

        override fun transmuteInitializer() = initializer.transmute()
        override fun wrap(): Value = UnorderedTupleValue(
            valueByKey = mapOf(
                Identifier.of("name") to lazyOf(name),
                Identifier.of("initializer") to lazyOf(initializer.wrap()),
            )
        )
    }

    data class FunctionDefinitionTerm(
        override val name: Identifier,
        val abstractionConstructor: AbstractionConstructorTerm,
    ) : DefinitionTerm() {
        companion object {
            fun build(
                ctx: SigmaParser.FunctionDefinitionContext,
            ): FunctionDefinitionTerm = FunctionDefinitionTerm(
                name = IdentifierTerm.build(ctx.name).toIdentifier(),
                abstractionConstructor = AbstractionConstructorTerm.build(
                    argumentTypeCtx = ctx.argumentType,
                    bodyCtx = ctx.body,
                ),
            )
        }

        override fun transmuteInitializer() = abstractionConstructor.transmute()

        override fun wrap(): Value = TODO()
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

    fun transform(): ExpressionBuilder<KnotConstructor> = object : ExpressionBuilder<KnotConstructor>() {
        override fun build(
            buildContext: Expression.BuildContext,
        ): KnotConstructor {
            val projectReference = buildContext.projectReference

            val rootScope = StaticScope.fixed(
                expressionByName = imports.associate {
                    it.effectiveName.transmute() to projectReference.resolveModule(
                        modulePath = it.importedModulePath,
                    )
                },
            )

            val rootContext = FormationContext(
                scope = rootScope,
            )

            val allLocalNames = definitions.fold(
                initial = emptySet<Identifier>()
            ) { accLocalNames, definitionTerm ->
                accLocalNames + definitionTerm.name
            }

            return LocalScope.Constructor.make { localScopeReference ->
                val innerContext = rootContext.withExtendedScope(
                    localNames = allLocalNames,
                    localScopeReference = localScopeReference,
                )

                definitions.mapUniquely {
                    LocalScope.Constructor.SimpleDefinition(
                        name = it.name,
                        initializer = it.transmuteInitializer().build(
                            formationContext = innerContext,
                            buildContext = buildContext,
                        ),
                    )
                }
            }.build(
                buildContext = buildContext,
            ).knotConstructor
        }
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("imports") to lazyOf(imports.wrap()),
            Identifier.of("definitions") to lazyOf(definitions.wrap()),
        )
    )
}
