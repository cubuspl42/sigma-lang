package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

class TypeVariableDefinition : Definition {
    val typeVariable = TypeVariable()

    val typePlaceholder: TypePlaceholder
        get() = typeVariable.toPlaceholder()

    override val bodyStub: Stub<Expression> = Stub.of(
        AtomicExpression.forType(type = typeVariable)
    )
}
