package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ImportStatementContext
import sigma.parser.antlr.SigmaParser.ModuleContext

data class ModuleSourceTerm(
    override val location: SourceLocation,
    val imports: List<Import>,
    val namespaceEntries: List<NamespaceEntrySourceTerm>,
) : SourceTerm() {
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
                    Import.build(it)
                },
                namespaceEntries = ctx.namespaceBody().namespaceEntry().map {
                    NamespaceEntrySourceTerm.build(it)
                },
            )
        }
    }

    data class Import(
        override val location: SourceLocation,
        val path: List<String>,
    ) : SourceTerm() {
        companion object {
            fun build(ctx: ImportStatementContext): Import {
                return Import(
                    location = SourceLocation.build(ctx),
                    path = ctx.importPath().identifier().map { it.text },
                )
            }
        }
    }
}
