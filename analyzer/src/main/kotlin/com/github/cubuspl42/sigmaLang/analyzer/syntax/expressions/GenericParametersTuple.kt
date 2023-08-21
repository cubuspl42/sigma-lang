package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.semantics.DynamicResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceTerm

data class GenericParametersTuple(
    override val location: SourceLocation,
    val parametersDefinitions: List<Symbol>,
) : SourceTerm() {
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