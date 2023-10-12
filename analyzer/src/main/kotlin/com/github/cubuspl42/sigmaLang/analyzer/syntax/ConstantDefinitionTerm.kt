package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

interface ConstantDefinitionTerm : NamespaceEntryTerm, DefinitionTerm {
    override val name: Identifier

    override val declaredTypeBody: ExpressionTerm?

    override val body: ExpressionTerm
}
