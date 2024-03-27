package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ListEmptyPattern
import com.github.cubuspl42.sigmaLang.core.ListUnconsPattern
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.Pattern
import com.github.cubuspl42.sigmaLang.core.TagPattern
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

sealed class PatternTerm {
    abstract val names: Set<Identifier>

    abstract fun transmute(
        initializerStub: ExpressionStub<Expression>,
    ): ExpressionStub<LocalScope.Constructor.Definition>
}

sealed class ComplexPatternTerm : PatternTerm() {
    abstract fun makePattern(): ExpressionStub<Pattern>
}

sealed class DestructuringPatternTerm : ComplexPatternTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.DestructuringPatternContext,
        ): DestructuringPatternTerm = object : SigmaParserBaseVisitor<DestructuringPatternTerm>() {
            override fun visitListEmptyPattern(
                ctx: SigmaParser.ListEmptyPatternContext,
            ): DestructuringPatternTerm = ListEmptyPatternTerm

            override fun visitListUnconsPattern(
                ctx: SigmaParser.ListUnconsPatternContext,
            ): DestructuringPatternTerm = ListUnconsPatternTerm.build(ctx)
        }.visit(ctx)
    }
}

data class ListUnconsPatternTerm(
    val headName: Identifier,
    val tailName: Identifier,
) : DestructuringPatternTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ListUnconsPatternContext,
        ): ListUnconsPatternTerm = ListUnconsPatternTerm(
            headName = IdentifierTerm.build(ctx.headName).transmute(),
            tailName = IdentifierTerm.build(ctx.tailName).transmute(),
        )
    }

    override fun makePattern(): ExpressionStub<Pattern> = ExpressionStub.pure(
        ListUnconsPattern(
            listClass = BuiltinModuleReference.listClass,
            headName = headName,
            tailName = tailName,
        ),
    )

    override val names: Set<Identifier>
        get() = setOf(headName, tailName)

    override fun transmute(
        initializerStub: ExpressionStub<Expression>,
    ) = object : ExpressionStub<LocalScope.Constructor.PatternDefinition>() {
        override fun transform(
            context: FormationContext,
        ) = LocalScope.Constructor.PatternDefinition(
            pattern = ListUnconsPattern(
                listClass = BuiltinModuleReference.listClass,
                headName = headName,
                tailName = tailName,
            ),
            initializer = initializerStub.build(
                formationContext = context,
            ),
        )
    }
}

data object ListEmptyPatternTerm : DestructuringPatternTerm() {
    override fun makePattern() = ExpressionStub.pure(
        ListEmptyPattern(
            listClass = BuiltinModuleReference.listClass,
        ),
    )

    override val names: Set<Identifier> = emptySet()

    override fun transmute(
        initializerStub: ExpressionStub<Expression>,
    ) = object : ExpressionStub<LocalScope.Constructor.PatternDefinition>() {
        override fun transform(
            context: FormationContext,
        ) = LocalScope.Constructor.PatternDefinition(
            pattern = ListEmptyPattern(
                listClass = BuiltinModuleReference.listClass,
            ),
            initializer = initializerStub.build(
                formationContext = context,
            ),
        )
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
        ) = TagPattern(
            class_ = class_.build(
                formationContext = context,
            ),
            newName = newName,
        )
    }

    override fun transmute(initializerStub: ExpressionStub<Expression>): ExpressionStub<LocalScope.Constructor.Definition> {
        TODO("Not yet implemented")
    }
}
