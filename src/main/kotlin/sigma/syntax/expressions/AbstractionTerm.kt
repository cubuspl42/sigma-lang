package sigma.syntax.expressions

import sigma.semantics.DeclarationScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeConstructorTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.semantics.types.TypeVariable
import sigma.syntax.Term
import sigma.evaluation.values.Symbol
import sigma.semantics.DeclarationBlock
import sigma.semantics.TypeDefinition

data class AbstractionTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
    val declaredImageType: TypeExpressionTerm? = null,
    val image: ExpressionTerm,
) : ExpressionTerm() {
    data class GenericParameterDefinition(
        override val name: Symbol,
        override val definedType: TypeVariable,
    ) : TypeDefinition {
        companion object {
            fun of(
                name: String,
            ): GenericParameterDefinition {
                val nameSymbol = Symbol.of(name)

                return GenericParameterDefinition(
                    name = nameSymbol,
                    definedType = TypeVariable(name = nameSymbol),
                )
            }
        }
    }

    data class GenericParametersTuple(
        override val location: SourceLocation,
        val parametersDefinitions: List<GenericParameterDefinition>,
    ) : Term() {
        companion object {
            fun build(
                ctx: GenericParametersTupleContext,
            ): GenericParametersTuple = GenericParametersTuple(
                location = SourceLocation.build(ctx),
                parametersDefinitions = ctx.genericParameterDeclaration().map {
                    GenericParameterDefinition.of(it.name.text)
                },
            )
        }

        inner class GenericParametersTupleBlock : DeclarationBlock() {
            override fun getDeclaration(
                name: Symbol,
            ): TypeDefinition? = parametersDefinitions.lastOrNull {
                it.name == name
            }
        }

        val asDeclarationBlock = GenericParametersTupleBlock()
    }

    companion object {
        fun build(
            ctx: AbstractionContext,
        ): AbstractionTerm = AbstractionTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                TypeExpressionTerm.build(it)
            },
            image = ExpressionTerm.build(ctx.image),
        )
    }

    override fun dump(): String = "(abstraction)"
}
