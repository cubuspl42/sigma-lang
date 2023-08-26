package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaUnorderedTupleTypeConstructorEntry
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaUnorderedTupleTypeConstructorEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaUnorderedTupleTypeConstructorEntry {

    override val asEntry: UnorderedTupleTypeConstructorTerm.Entry = object : UnorderedTupleTypeConstructorTerm.Entry {
        override val name: Symbol
            get() = Symbol.of(this@SigmaUnorderedTupleTypeConstructorEntryImplMixin.declaredName.text)

        override val type: ExpressionTerm
            get() = this@SigmaUnorderedTupleTypeConstructorEntryImplMixin.declaredType.expression.asTerm
    }
}
