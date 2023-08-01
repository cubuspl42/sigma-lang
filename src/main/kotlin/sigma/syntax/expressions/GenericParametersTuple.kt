package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.asThunk
import sigma.parser.antlr.SigmaParser
import sigma.semantics.DynamicResolution
import sigma.semantics.Formula
import sigma.semantics.ResolvedName
import sigma.semantics.StaticBlock
import sigma.semantics.types.MetaType
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
                type = MetaType.asThunk,
                resolution = DynamicResolution(
                    resolvedFormula = Formula(
                        name = name,
                    ),
                ),
            )
        } else {
            null
        }
    }

    val typeVariables: Set<TypeVariable>
        get() = parametersDefinitions.map {
            TypeVariable(
                formula = Formula(
                    name = it,
                )
            )
        }.toSet()

    val asDeclarationBlock = GenericParametersTupleBlock()
}
