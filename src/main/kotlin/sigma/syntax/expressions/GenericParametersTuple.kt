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
    val parametersDefinitions: List<Symbol>,
) : Term() {
    companion object {
        fun build(
            ctx: SigmaParser.GenericParametersTupleContext,
        ): GenericParametersTuple = GenericParametersTuple(
            location = SourceLocation.build(ctx),
            parametersDefinitions = ctx.genericParameterDeclaration().map {
                Symbol.of(it.name.text)
            },
        )
    }

    inner class GenericParametersTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = if (parametersDefinitions.any { it == name }) {
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
