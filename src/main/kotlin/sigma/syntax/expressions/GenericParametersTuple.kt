package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser
import sigma.semantics.DeclarationBlock
import sigma.semantics.TypeEntityDefinition
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
        val definedTypeVariable: TypeVariable,
    ) : TypeEntityDefinition {
        companion object {
            fun of(
                name: String,
            ): GenericParameterDefinition {
                val nameSymbol = Symbol.of(name)

                return GenericParameterDefinition(
                    name = nameSymbol,
                    definedTypeVariable = TypeVariable(name = nameSymbol),
                )
            }
        }

        override val definedTypeEntity = definedTypeVariable
    }

    inner class GenericParametersTupleBlock : DeclarationBlock() {
        override fun getDeclaration(
            name: Symbol,
        ): TypeEntityDefinition? = parametersDefinitions.lastOrNull {
            it.name == name
        }
    }

    val asDeclarationBlock = GenericParametersTupleBlock()
}
