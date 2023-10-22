package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class TypeSpecificationSourceTerm(
    override val location: SourceLocation,
    val subject: ExpressionTerm,
    val argument: TupleConstructorTerm,
) : ExpressionSourceTerm(), TypeSpecificationTerm {
    override fun dump(): String = "${subject.dump()}!${argument.dump()}"
}
