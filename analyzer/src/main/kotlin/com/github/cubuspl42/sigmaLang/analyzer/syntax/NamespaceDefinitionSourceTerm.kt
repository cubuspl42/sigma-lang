package com.github.cubuspl42.sigmaLang.analyzer.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.NamespaceDefinitionContext

data class NamespaceDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Identifier,
    override val entries: List<NamespaceEntrySourceTerm>,
) : NamespaceEntrySourceTerm(), NamespaceDefinitionTerm {
    companion object {
        fun parse(
            source: String,
        ): NamespaceDefinitionSourceTerm {
            val sourceName = "__namespace_definition__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.namespaceDefinition())
        }

        fun build(
            ctx: NamespaceDefinitionContext,
        ): NamespaceDefinitionSourceTerm = NamespaceDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Identifier.of(ctx.name.text),
            entries = ctx.namespaceBody().namespaceEntry().map {
                NamespaceEntrySourceTerm.build(it)
            },
        )
    }
}
