package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser
import sigma.semantics.DeclarationBlock
import sigma.semantics.TypeDefinition
import sigma.semantics.types.TypeVariable
import sigma.syntax.SourceLocation
import sigma.syntax.Term

data class GenericParametersTuple(
    override val location: SourceLocation,
    val parametersDefinitions: List<GenericParameterDefinition>,
) : Term() {
    companion object {
        fun build(
            ctx: SigmaParser.GenericParametersTupleContext,
        ): GenericParametersTuple = GenericParametersTuple(
            location = SourceLocation.build(ctx),
            parametersDefinitions = ctx.genericParameterDeclaration().map {
                GenericParameterDefinition.of(it.name.text)
            },
        )
    }

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

    inner class GenericParametersTupleBlock : DeclarationBlock() {
        override fun getDeclaration(
            name: Symbol,
        ): TypeDefinition? = parametersDefinitions.lastOrNull {
            it.name == name
        }
    }

    val asDeclarationBlock = GenericParametersTupleBlock()
}
