package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaUnorderedTupleTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaUnorderedTupleTypeConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaUnorderedTupleTypeConstructor {
    override val asTerm: UnorderedTupleTypeConstructorTerm =
        object : PsiExpressionTerm(), UnorderedTupleTypeConstructorTerm {
            override val entries: List<UnorderedTupleTypeConstructorTerm.Entry>
                get() = this@SigmaUnorderedTupleTypeConstructorImplMixin.unorderedTupleTypeConstructorEntryList.map {
                    it.asEntry
                }
        }
}
