package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock

sealed interface TupleTypeConstructorTerm : ExpressionTerm {
    fun toArgumentDeclarationBlock(
        argumentDeclaration: AbstractionConstructorTerm.ArgumentDeclaration,
    ): StaticBlock
}
