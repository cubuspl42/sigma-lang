package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.ClassDefinitionContext
import sigma.parser.antlr.SigmaParser.FieldDeclarationContext
import sigma.parser.antlr.SigmaParser.MethodDefinitionContext
import sigma.syntax.expressions.ExpressionSourceTerm

data class ClassDefinitionSourceTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val fieldDeclarations: List<FieldDeclarationSourceTerm>,
    val methodDefinitions: List<MethodDefinitionSourceTerm>,
) : NamespaceEntrySourceTerm() {
    data class FieldDeclarationSourceTerm(
        override val location: SourceLocation,
        val name: Symbol,
        val type: ExpressionSourceTerm,
    ) : SourceTerm() {
        companion object {
            fun build(
                ctx: FieldDeclarationContext,
            ): FieldDeclarationSourceTerm = FieldDeclarationSourceTerm(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
                type = ExpressionSourceTerm.build(ctx.type),
            )
        }
    }

    data class MethodDefinitionSourceTerm(
        override val location: SourceLocation,
        val name: Symbol,
        val body: ExpressionSourceTerm,
    ) : SourceTerm() {
        companion object {
            fun build(
                ctx: MethodDefinitionContext,
            ): MethodDefinitionSourceTerm = MethodDefinitionSourceTerm(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
                body = ExpressionSourceTerm.build(ctx.body),
            )
        }
    }

    companion object {
        fun build(
            ctx: ClassDefinitionContext,
        ): ClassDefinitionSourceTerm = ClassDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            fieldDeclarations = ctx.fieldDeclaration().map {
                FieldDeclarationSourceTerm.build(it)
            },
            methodDefinitions = ctx.methodDefinition().map {
                MethodDefinitionSourceTerm.build(it)
            },
        )
    }
}
