package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedUnorderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor

interface UnorderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    interface Entry {
        val name: Identifier

        val type: ExpressionTerm
    }

    val entries: List<Entry>

    override fun toArgumentDeclarationBlock(
        argumentDeclaration: AbstractionConstructor.ArgumentDeclaration,
    ): StaticBlock = StaticBlock.Fixed(
        resolvedNameByName = entries.associate { entry ->
            entry.name to LeveledResolvedIntroduction(
                level = StaticScope.Level.Primary,
                resolvedIntroduction = ResolvedUnorderedArgument(
                    argumentDeclaration = argumentDeclaration,
                    name = entry.name,
                ),
            )
        },
    )
}
