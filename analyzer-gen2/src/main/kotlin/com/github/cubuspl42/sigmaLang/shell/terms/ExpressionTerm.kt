package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

sealed interface ExpressionTerm : Term {
    companion object : Term.Builder<SigmaParser.ExpressionContext, ExpressionTerm>() {
        override fun build(
            ctx: SigmaParser.ExpressionContext,
        ): ExpressionTerm = object : SigmaParserBaseVisitor<ExpressionTerm>() {
            override fun visitIsAExpressionAlt(
                ctx: SigmaParser.IsAExpressionAltContext,
            ): ExpressionTerm = IsATerm.build(ctx)

            override fun visitConcatExpressionAlt(
                ctx: SigmaParser.ConcatExpressionAltContext,
            ): ExpressionTerm = ConcatTerm.build(ctx)

            override fun visitCalleeExpressionAlt(
                ctx: SigmaParser.CalleeExpressionAltContext,
            ): ExpressionTerm = build(ctx.callee())

            override fun visitReferenceCallableExpressionAlt(
                ctx: SigmaParser.ReferenceCallableExpressionAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())

            override fun visitTupleConstructorExpressionAlt(
                ctx: SigmaParser.TupleConstructorExpressionAltContext,
            ): ExpressionTerm = TupleConstructorTerm.build(ctx.tupleConstructor())

            override fun visitStringLiteralExpressionAlt(
                ctx: SigmaParser.StringLiteralExpressionAltContext,
            ): ExpressionTerm = StringLiteralTerm.build(ctx.stringLiteral())

            override fun visitAbstractionConstructorExpressionAlt(
                ctx: SigmaParser.AbstractionConstructorExpressionAltContext,
            ): ExpressionTerm = AbstractionConstructorTerm.build(ctx.abstractionConstructor())

            override fun visitBooleanLiteralExpressionAlt(
                ctx: SigmaParser.BooleanLiteralExpressionAltContext,
            ): ExpressionTerm = BooleanLiteralTerm.build(ctx.booleanLiteral())

            override fun visitLetInExpressionAlt(
                ctx: SigmaParser.LetInExpressionAltContext,
            ): ExpressionTerm = LetInTerm.build(ctx.letIn())

            override fun visitWhenExpressionAlt(
                ctx: SigmaParser.WhenExpressionAltContext,
            ): ExpressionTerm = WhenTerm.build(ctx.`when`())
        }.visit(ctx)

        fun build(
            ctx: SigmaParser.CalleeContext,
        ): ExpressionTerm = object : SigmaParserBaseVisitor<ExpressionTerm>() {
            override fun visitCallCallableExpressionAlt(
                ctx: SigmaParser.CallCallableExpressionAltContext,
            ): ExpressionTerm = CallTerm.build(ctx)

            override fun visitFieldReadCallableExpressionAlt(
                ctx: SigmaParser.FieldReadCallableExpressionAltContext,
            ): ExpressionTerm = FieldReadTerm.build(ctx)

            override fun visitReferenceCallableExpressionAlt(
                ctx: SigmaParser.ReferenceCallableExpressionAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())
        }.visit(ctx)

        override fun extract(
            parser: SigmaParser,
        ): SigmaParser.ExpressionContext = parser.expression()
    }

    fun transmute(): ExpressionStub<ShadowExpression>
}
