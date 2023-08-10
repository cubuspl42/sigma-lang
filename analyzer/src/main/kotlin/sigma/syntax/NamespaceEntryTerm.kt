package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParserBaseVisitor

// TODO: Rename
sealed class NamespaceEntryTerm : Term() {
    companion object {
        fun parse(
            source: String,
        ): NamespaceEntryTerm {
            val sourceName = "__namespace_entry__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return NamespaceEntryTerm.build(parser.namespaceEntry())
        }

        fun build(
            ctx: SigmaParser.NamespaceEntryContext,
        ): NamespaceEntryTerm = object : SigmaParserBaseVisitor<NamespaceEntryTerm>() {
            override fun visitConstantDefinition(
                ctx: SigmaParser.ConstantDefinitionContext,
            ): NamespaceEntryTerm = ConstantDefinitionTerm.build(ctx)

            override fun visitClassDefinition(
                ctx: SigmaParser.ClassDefinitionContext,
            ): NamespaceEntryTerm = ClassDefinitionTerm.build(ctx)

            override fun visitNamespaceDefinition(
                ctx: SigmaParser.NamespaceDefinitionContext,
            ): NamespaceEntryTerm = NamespaceDefinitionTerm.build(ctx)
        }.visit(ctx)
    }
}
