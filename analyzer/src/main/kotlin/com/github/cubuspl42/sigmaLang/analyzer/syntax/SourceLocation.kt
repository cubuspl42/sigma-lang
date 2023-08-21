package com.github.cubuspl42.sigmaLang.analyzer.syntax

import org.antlr.v4.runtime.ParserRuleContext

data class SourceLocation(
    val lineIndex: Int,
    val columnIndex: Int,
) {
    companion object {
        fun build(
            ctx: ParserRuleContext,
        ): SourceLocation = SourceLocation(
            lineIndex = ctx.start.line,
            columnIndex = ctx.start.charPositionInLine,
        )
    }

    override fun toString(): String = "[Ln ${lineIndex}, Col ${columnIndex}]"
}
