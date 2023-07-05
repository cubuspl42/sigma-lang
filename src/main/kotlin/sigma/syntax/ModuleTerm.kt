package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ImportStatementContext
import sigma.parser.antlr.SigmaParser.ModuleContext

data class ModuleTerm(
    override val location: SourceLocation,
    val imports: List<Import>,
    val staticStatements: List<StaticStatementTerm>,
) : Term() {
    companion object {
        fun parse(
            source: String,
        ): ModuleTerm {
            val sourceName = "__module__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.module())
        }

        fun build(ctx: ModuleContext): ModuleTerm {
            return ModuleTerm(
                location = SourceLocation.build(ctx),
                imports = ctx.importSection().importStatement().map {
                    Import.build(it)
                },
                staticStatements = ctx.moduleBody().staticStatement().map {
                    StaticStatementTerm.build(it)
                },
            )
        }
    }

    data class Import(
        override val location: SourceLocation,
        val path: List<String>,
    ) : Term() {
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
