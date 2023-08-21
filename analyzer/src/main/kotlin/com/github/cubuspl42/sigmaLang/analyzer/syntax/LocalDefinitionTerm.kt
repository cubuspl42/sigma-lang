package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

interface LocalDefinitionTerm : DefinitionTerm {
    override val name: Symbol

    override val declaredTypeBody: ExpressionTerm?

    override val body: ExpressionTerm
}
