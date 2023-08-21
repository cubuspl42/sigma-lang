package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Program
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceTerm

data class LocalScopeSourceTerm(
    override val location: SourceLocation,
    val definitions: List<LocalDefinitionSourceTerm>,
) : SourceTerm() {
    companion object {
        fun parse(
            sourceName: String,
            source: String,
        ): LocalScopeSourceTerm = build(
            ctx = Program.buildParser(
                sourceName = sourceName,
                source = source,
            ).localScope(),
        )

        fun build(
            ctx: SigmaParser.LocalScopeContext,
        ): LocalScopeSourceTerm = LocalScopeSourceTerm(
            location = SourceLocation.build(ctx),
            definitions = ctx.definition().map {
                LocalDefinitionSourceTerm.build(it)
            },
        )
    }
}
