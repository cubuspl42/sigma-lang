package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaUnorderedTupleConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaUnorderedTupleConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaUnorderedTupleConstructor {
    override val asTerm: UnorderedTupleConstructorTerm = object : PsiExpressionTerm(), UnorderedTupleConstructorTerm {
        override val entries: List<UnorderedTupleConstructorTerm.Entry>
            get() = this@SigmaUnorderedTupleConstructorImplMixin.unorderedTupleConstructorEntryList.map {
                it.asEntry
            }
    }
}
