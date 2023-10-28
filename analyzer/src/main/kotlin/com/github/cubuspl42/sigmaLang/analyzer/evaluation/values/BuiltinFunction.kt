package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

abstract class BuiltinFunctionConstructor : AtomicExpression() {
    final override val valueThunk: Thunk<Value> by lazy { Thunk.pure(function) }

    final override val type: FunctionType by lazy {
        UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )
    }

    abstract val function: FunctionValue

    abstract val argumentType: TableType

    abstract val imageType: SpecificType
}
