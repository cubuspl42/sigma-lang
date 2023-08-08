package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ImportStatementContext
import sigma.parser.antlr.SigmaParser.ModuleContext
import sigma.parser.antlr.SigmaParser.NamespaceDefinitionContext

data class NamespaceDefinitionTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val staticStatements: List<StaticStatementTerm>,
) : StaticStatementTerm() {
    companion object {
        fun parse(
            source: String,
        ): NamespaceDefinitionTerm {
            val sourceName = "__namespace_definition__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.namespaceDefinition())
        }

        fun build(
            ctx: NamespaceDefinitionContext,
        ): NamespaceDefinitionTerm = NamespaceDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            staticStatements = ctx.namespaceBody().staticStatement().map {
                StaticStatementTerm.build(it)
            },
        )
    }
}
