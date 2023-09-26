package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
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

    class TypeVariableDefinition(
        override val name: Symbol,
    ) : ConstantDefinition() {
        override val valueThunk: Thunk<Value> = Thunk.pure(
            TypeVariable(
                formula = Formula(name = name),
            ).asValue
        )

        override val computedEffectiveType: Expression.Computation<MembershipType> = Expression.Computation.pure(MetaType)
    }

    inner class GenericParametersTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ClassifiedIntroduction? = if (parametersDefinitions.any { it == name }) {
            TypeVariableDefinition(
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
