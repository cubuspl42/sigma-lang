package com.github.cubuspl42.sigmaLang.analyzer.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ImportStatementContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ModuleContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath
import java.lang.IllegalArgumentException

data class ModuleSourceTerm(
    override val location: SourceLocation,
    override val imports: List<ImportSourceTerm>,
    override val namespaceEntries: List<NamespaceEntrySourceTerm>,
) : SourceTerm(), ModuleTerm {
    companion object {
        fun parse(
            source: String,
        ): ModuleSourceTerm {
            val sourceName = "__module__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.module())
        }

        fun build(ctx: ModuleContext): ModuleSourceTerm {
            return ModuleSourceTerm(
                location = SourceLocation.build(ctx),
                imports = ctx.importSection().importStatement().map {
                    ImportSourceTerm.build(it)
                },
                namespaceEntries = ctx.namespaceBody().namespaceEntry().map {
                    NamespaceEntrySourceTerm.build(it)
                },
            )
        }
    }

    data class ImportSourceTerm(
        override val location: SourceLocation,
        override val modulePath: ModulePath,
    ) : SourceTerm(), ImportTerm {
        companion object {
            fun build(ctx: ImportStatementContext): ImportSourceTerm {
                val pathSegments = ctx.importPath().packagePathSegment
                val moduleName = ctx.importPath().moduleName.text

                if (pathSegments.isNotEmpty()) {
                    throw IllegalArgumentException()
                }

                return ImportSourceTerm(
                    location = SourceLocation.build(ctx),
                    modulePath = ModulePath.root(name = moduleName),
                )
            }
        }
    }
}
