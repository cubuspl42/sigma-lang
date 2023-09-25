package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericParametersTuple

class GenericTypeConstructor(
    val context: StaticScope,
    val argumentMetaType: GenericParametersTuple,
    val bodyTerm: ExpressionSourceTerm,
    val body: TypeEntity,
) : TypeEntity() {
    fun call(passedArgument: OrderedTypeTuple): TypeEntity {
        // TODO
//        return bodyTerm.evaluate(declarationScope = context)
        TODO()
    }
}
