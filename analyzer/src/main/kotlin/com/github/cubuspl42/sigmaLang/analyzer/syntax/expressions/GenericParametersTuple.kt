package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration
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
    ) : UserDeclaration {
        override val annotatedTypeThunk: Thunk<Type> = Thunk.pure(MetaType)

        override val effectiveTypeThunk: Thunk<Type> = annotatedTypeThunk

        override val errors: Set<SemanticError> = emptySet()
    }

    inner class GenericParametersTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ClassifiedIntroduction? = if (parametersDefinitions.any { it == name }) {
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
