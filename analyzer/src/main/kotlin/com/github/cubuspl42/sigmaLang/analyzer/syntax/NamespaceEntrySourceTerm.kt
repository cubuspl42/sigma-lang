package com.github.cubuspl42.sigmaLang.analyzer.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor

sealed class NamespaceEntrySourceTerm : SourceTerm(), NamespaceEntryTerm {
    companion object {
        fun parse(
            source: String,
        ): NamespaceEntrySourceTerm {
            val sourceName = "__namespace_entry__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return NamespaceEntrySourceTerm.build(parser.namespaceEntry())
        }

        fun build(
            ctx: SigmaParser.NamespaceEntryContext,
        ): NamespaceEntrySourceTerm = object : SigmaParserBaseVisitor<NamespaceEntrySourceTerm>() {
            override fun visitConstantDefinition(
                ctx: SigmaParser.ConstantDefinitionContext,
            ): NamespaceEntrySourceTerm = ConstantDefinitionSourceTerm.build(ctx)

            override fun visitMetaDefinition(
                ctx: SigmaParser.MetaDefinitionContext,
            ): NamespaceEntrySourceTerm = MetaDefinitionSourceTerm.build(ctx)

            override fun visitClassDefinition(
                ctx: SigmaParser.ClassDefinitionContext,
            ): NamespaceEntrySourceTerm = ClassDefinitionSourceTerm.build(ctx)

            override fun visitNamespaceDefinition(
                ctx: SigmaParser.NamespaceDefinitionContext,
            ): NamespaceEntrySourceTerm = NamespaceDefinitionSourceTerm.build(ctx)
        }.visit(ctx)
    }
}
