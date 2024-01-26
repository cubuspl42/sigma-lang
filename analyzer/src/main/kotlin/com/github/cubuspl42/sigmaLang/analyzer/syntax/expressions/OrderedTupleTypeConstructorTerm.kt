package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedOrderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope

interface OrderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    interface Element {
        val name: Identifier?

        val type: ExpressionTerm
    }

    val elements: List<Element>

    override fun toArgumentDeclarationBlock(
        argumentDeclaration: AbstractionConstructorTerm.ArgumentDeclaration,
    ): StaticBlock = StaticBlock.Fixed(
        resolvedNameByName = elements.mapIndexedNotNull { index, element ->
            element.name?.let { name ->
                name to LeveledResolvedIntroduction(
                    level = StaticScope.Level.Primary,
                    resolvedIntroduction = ResolvedOrderedArgument(
                        argumentDeclaration = argumentDeclaration,
                        index = index.toLong(),
                    ),
                )
            }
        }.toMap(),
    )
}
