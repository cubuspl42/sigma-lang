package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ListEmptyPattern
import com.github.cubuspl42.sigmaLang.core.ListUnconsPattern
import com.github.cubuspl42.sigmaLang.core.Pattern
import com.github.cubuspl42.sigmaLang.core.TagPattern
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

sealed class PatternTerm {
    abstract val names: Set<Identifier>

    abstract fun transmute(
        context: TransmutationContext,
    ): Pattern
}

sealed class DestructuringPatternTerm : PatternTerm() {
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
            headName = IdentifierTerm.build(ctx.headName).toIdentifier(),
            tailName = IdentifierTerm.build(ctx.tailName).toIdentifier(),
        )
    }

    override val names: Set<Identifier>
        get() = setOf(headName, tailName)

    override fun transmute(
        context: TransmutationContext,
    ): Pattern = ListUnconsPattern(
        listClass = BuiltinModuleReference.listClass,
        headName = headName,
        tailName = tailName,
    )
}

data object ListEmptyPatternTerm : DestructuringPatternTerm() {
    override val names: Set<Identifier> = emptySet()

    override fun transmute(
        context: TransmutationContext,
    ) = ListEmptyPattern(
        listClass = BuiltinModuleReference.listClass,
    )
}

data class TagPatternTerm(
    @Suppress("PropertyName") val class_: ExpressionTerm,
    val newName: Identifier,
) : PatternTerm() {
    companion object {
        fun build(ctx: SigmaParser.TagPatternContext): TagPatternTerm = TagPatternTerm(
            class_ = ExpressionTerm.build(ctx.class_),
            newName = IdentifierTerm.build(ctx.newName).transmute(),
        )
    }

    override val names: Set<Identifier>
        get() = setOf(newName)

    override fun transmute(
        context: TransmutationContext,
    ) = TagPattern(
        class_ = class_.transmute(
            context = context,
        ),
        newName = newName,
    )
}
