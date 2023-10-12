package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

interface ClassDefinitionTerm : NamespaceEntryTerm {
    override val name: Identifier

    val body: UnorderedTupleTypeConstructorTerm
}
