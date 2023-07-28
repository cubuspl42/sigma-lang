package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser
import sigma.semantics.Computation
import sigma.semantics.DynamicResolution
import sigma.semantics.ResolvedName
import sigma.semantics.StaticBlock
import sigma.semantics.ValueDeclaration
import sigma.semantics.types.MetaType
import sigma.semantics.types.Type
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
    ) : ValueDeclaration {
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

        override val effectiveValueType: Computation<Type>
            get() = TODO("Not yet implemented")
    }

    inner class GenericParametersTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = if (parametersDefinitions.any { it.name == name }) {
            ResolvedName(
                type = MetaType,
                resolution = DynamicResolution(),
            )
        } else {
             null
        }
    }

    val asDeclarationBlock = GenericParametersTupleBlock()
}
