package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.ClassDefinitionContext
import sigma.parser.antlr.SigmaParser.FieldDeclarationContext
import sigma.parser.antlr.SigmaParser.MethodDefinitionContext
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TupleTypeConstructorTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class ClassDefinitionTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val fieldDeclarations: List<FieldDeclarationTerm>,
    val methodDefinitions: List<MethodDefinitionTerm>,
) : StaticStatementTerm() {
    data class FieldDeclarationTerm(
        override val location: SourceLocation,
        val name: Symbol,
        val type: TypeExpressionTerm,
    ) : Term() {
        companion object {
            fun build(
                ctx: FieldDeclarationContext,
            ): FieldDeclarationTerm = FieldDeclarationTerm(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
                type = TypeExpressionTerm.build(ctx.type),
            )
        }
    }

    data class MethodDefinitionTerm(
        override val location: SourceLocation,
        val name: Symbol,
        val body: ExpressionTerm,
    ) : Term() {
        companion object {
            fun build(
                ctx: MethodDefinitionContext,
            ): MethodDefinitionTerm = MethodDefinitionTerm(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
                body = ExpressionTerm.build(ctx.body),
            )
        }
    }

    companion object {
        fun build(
            ctx: ClassDefinitionContext,
        ): ClassDefinitionTerm = ClassDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            fieldDeclarations = ctx.fieldDeclaration().map {
                FieldDeclarationTerm.build(it)
            },
            methodDefinitions = ctx.methodDefinition().map {
                MethodDefinitionTerm.build(it)
            },
        )
    }
}
