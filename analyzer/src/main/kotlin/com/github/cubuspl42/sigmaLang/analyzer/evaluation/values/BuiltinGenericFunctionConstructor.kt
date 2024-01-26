package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm

abstract class BuiltinGenericFunctionConstructor : AtomicExpression() {
    override val type: GenericType by lazy {
        GenericType(
            parameterDeclaration = parameterDeclaration,
            bodyType = body.type,
        )
    }

    override val valueThunk: Thunk<Value>
        get() = body.valueThunk

    abstract val parameterDeclaration: AbstractionConstructorTerm.ArgumentDeclaration

    abstract val body: BuiltinFunctionConstructor
}
