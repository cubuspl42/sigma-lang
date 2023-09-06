package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

interface ClassDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val body: UnorderedTupleTypeConstructorTerm
}
