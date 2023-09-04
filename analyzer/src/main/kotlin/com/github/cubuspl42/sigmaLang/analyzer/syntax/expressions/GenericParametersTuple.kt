package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvableDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
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

    class GenericParameterDeclaration(
        override val name: Symbol,
    ) : VariableDeclaration() {
        override val declaredTypeThunk: Thunk<Type> = Thunk.pure(MetaType)
    }

    inner class GenericParametersTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvableDeclaration? = if (parametersDefinitions.any { it == name }) {
            GenericParameterDeclaration(
                name = name,
            )
        } else {
            null
        }

        override fun getLocalNames(): Set<Symbol> = parametersDefinitions.toSet()
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
