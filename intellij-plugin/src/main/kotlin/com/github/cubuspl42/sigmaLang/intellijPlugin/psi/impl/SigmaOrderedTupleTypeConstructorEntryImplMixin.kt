package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaOrderedTupleTypeConstructorEntry
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaOrderedTupleTypeConstructorEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaOrderedTupleTypeConstructorEntry {

    override val asElement: OrderedTupleTypeConstructorTerm.Element = object : OrderedTupleTypeConstructorTerm.Element {
        override val name: Symbol?
            get() = this@SigmaOrderedTupleTypeConstructorEntryImplMixin.declaredName?.let {
                Symbol.of(it.text)
            }

        override val type: ExpressionTerm
            get() = this@SigmaOrderedTupleTypeConstructorEntryImplMixin.typeExpression.expression.asTerm
    }
}
