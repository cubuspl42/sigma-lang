package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParserBaseVisitor

// TODO: Rename
sealed class StaticStatementTerm : Term() {
    companion object {
        fun parse(
            source: String,
        ): StaticStatementTerm {
            val sourceName = "__static_statement__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return StaticStatementTerm.build(parser.staticStatement())
        }

        fun build(
            ctx: SigmaParser.StaticStatementContext,
        ): StaticStatementTerm = object : SigmaParserBaseVisitor<StaticStatementTerm>() {
            override fun visitTypeAliasDefinition(
                ctx: SigmaParser.TypeAliasDefinitionContext,
            ): StaticStatementTerm = TypeAliasDefinitionTerm.build(ctx)

            override fun visitConstantDefinition(
                ctx: SigmaParser.ConstantDefinitionContext,
            ): StaticStatementTerm = ConstantDefinitionTerm.build(ctx)

            override fun visitClassDefinition(
                ctx: SigmaParser.ClassDefinitionContext,
            ): StaticStatementTerm = ClassDefinitionTerm.build(ctx)
        }.visit(ctx)
    }
}
