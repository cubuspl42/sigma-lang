package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.*
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaUnorderedTupleConstructorEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaUnorderedTupleConstructorEntry {

    override val asEntry: UnorderedTupleConstructorTerm.Entry = object : UnorderedTupleConstructorTerm.Entry {
        override val name: Symbol
            get() = Symbol.of(this@SigmaUnorderedTupleConstructorEntryImplMixin.passedName.text)

        override val value: ExpressionTerm
            get() = this@SigmaUnorderedTupleConstructorEntryImplMixin.passedValue.asTerm
    }
}
