package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue

abstract class TypeConstructorConstructor : BuiltinFunctionConstructor() {
    final override val imageType: SpecificType = TypeType

    override val function: FunctionValue = object : FunctionValue() {
        override fun apply(argument: Value): Thunk<Value> = Thunk.pure(
            applyType(argument = argument).asValue
        )

        override fun dump(): String = this@TypeConstructorConstructor.dump()
    }


    abstract fun applyType(argument: Value): TypeAlike

    abstract fun dump(): String
}
