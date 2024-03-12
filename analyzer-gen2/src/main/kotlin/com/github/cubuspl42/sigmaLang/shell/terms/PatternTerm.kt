package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ListUnconsPattern
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.Pattern
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.TagPattern
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.map

sealed class PatternTerm {
    companion object {
        fun build(
            ctx: SigmaParser.PatternContext,
        ): PatternTerm = object : SigmaParserBaseVisitor<PatternTerm>() {
            override fun visitIdentityPattern(
                ctx: SigmaParser.IdentityPatternContext,
            ): PatternTerm = IdentityPatternTerm.build(ctx)

            override fun visitListUnconsPattern(
                ctx: SigmaParser.ListUnconsPatternContext,
            ): PatternTerm = ListUnconsPatternTerm.build(ctx)

            override fun visitTagPattern(
                ctx: SigmaParser.TagPatternContext,
            ): PatternTerm = TagPatternTerm.build(ctx)
        }.visit(ctx)
    }

    abstract val names: Set<Identifier>

    abstract fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ): ExpressionStub<LocalScope.Constructor.Definition>

}

data class IdentityPatternTerm(
    val name: Identifier,
) : PatternTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.IdentityPatternContext,
        ): IdentityPatternTerm = IdentityPatternTerm(
            name = IdentifierTerm.build(ctx.name).transmute(),
        )
    }

    override val names: Set<Identifier>
        get() = setOf(name)

    override fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ): ExpressionStub<LocalScope.Constructor.Definition> = initializerStub.map { initializer ->
        LocalScope.Constructor.SimpleDefinition(
            name = name,
            initializer = initializer,
        )
    }
}

sealed class ComplexPatternTerm : PatternTerm() {
    abstract fun makePattern(): ExpressionStub<Pattern>
}

data class ListUnconsPatternTerm(
    val headName: Identifier,
    val tailName: Identifier,
) : ComplexPatternTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ListUnconsPatternContext,
        ): ListUnconsPatternTerm = ListUnconsPatternTerm(
            headName = IdentifierTerm.build(ctx.headName).transmute(),
            tailName = IdentifierTerm.build(ctx.tailName).transmute(),
        )
    }

    override fun makePattern() = object : ExpressionBuilder<Pattern>() {
        override fun build(
            buildContext: Expression.BuildContext,
        ): Pattern = ListUnconsPattern(
            listClass = buildContext.builtinModule.listClass,
            headName = headName,
            tailName = tailName,
        )
    }.asStub()

    override val names: Set<Identifier>
        get() = setOf(headName, tailName)

    override fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ) = object : ExpressionStub<LocalScope.Constructor.PatternDefinition>() {
        override fun transform(
            context: FormationContext,
        ) = object : ExpressionBuilder<LocalScope.Constructor.PatternDefinition>() {
            override fun build(buildContext: Expression.BuildContext) = LocalScope.Constructor.PatternDefinition(
                builtinModuleReference = buildContext.builtinModule,
                pattern = ListUnconsPattern(
                    listClass = buildContext.builtinModule.listClass,
                    headName = headName,
                    tailName = tailName,
                ),
                initializer = initializerStub.build(
                    formationContext = context,
                    buildContext = buildContext,
                ),
            )
        }
    }
}

data class TagPatternTerm(
    @Suppress("PropertyName") val class_: ExpressionTerm,
    val newName: Identifier,
) : ComplexPatternTerm() {
    companion object {
        fun build(ctx: SigmaParser.TagPatternContext): TagPatternTerm = TagPatternTerm(
            class_ = ExpressionTerm.build(ctx.class_),
            newName = IdentifierTerm.build(ctx.newName).transmute(),
        )
    }

    override val names: Set<Identifier>
        get() = setOf(newName)

    override fun makePattern(): ExpressionStub<Pattern> = object : ExpressionStub<Pattern>() {
        override fun transform(
            context: FormationContext,
        ): ExpressionBuilder<Pattern> = object : ExpressionBuilder<Pattern>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Pattern = TagPattern(
                builtinModuleReference = buildContext.builtinModule,
                class_ = class_.build(
                    formationContext = context,
                    buildContext = buildContext,
                ),
                newName = newName,
            )
        }
    }

    override fun transmute(initializerStub: ExpressionStub<ShadowExpression>): ExpressionStub<LocalScope.Constructor.Definition> {
        TODO("Not yet implemented")
    }
}
