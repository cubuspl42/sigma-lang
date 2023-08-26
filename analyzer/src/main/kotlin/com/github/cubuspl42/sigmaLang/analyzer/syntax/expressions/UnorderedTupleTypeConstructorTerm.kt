package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface UnorderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    val entries: List<UnorderedTupleConstructorTerm.Entry>
}
