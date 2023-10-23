package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor

sealed interface TupleTypeConstructorTerm : ExpressionTerm {
    fun toArgumentDeclarationBlock(
        argumentDeclaration: AbstractionConstructor.ArgumentDeclaration,
    ): StaticBlock
}
