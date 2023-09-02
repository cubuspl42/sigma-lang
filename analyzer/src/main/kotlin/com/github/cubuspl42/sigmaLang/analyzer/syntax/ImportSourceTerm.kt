package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ImportStatementContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath

data class ImportSourceTerm(
    override val location: SourceLocation,
    override val modulePath: ModulePath,
) : SourceTerm(), ImportTerm {
    companion object {
        fun parse(
            source: String,
        ): ImportSourceTerm = SourceTerm.parse(
            source = source,
            sourceName = "__import__",
            rule = SigmaParser::importStatement,
            build = ::build,
        )

        fun build(
            ctx: ImportStatementContext,
        ): ImportSourceTerm {
            val importPathCtx = ctx.importPath()

            if (importPathCtx.packagePathSegment.isNotEmpty()) {
                throw IllegalArgumentException()
            }

            return ImportSourceTerm(
                location = SourceLocation.build(ctx),
                modulePath = ModulePath(
                    name = importPathCtx.moduleName.text,
                ),
            )
        }
    }
}
