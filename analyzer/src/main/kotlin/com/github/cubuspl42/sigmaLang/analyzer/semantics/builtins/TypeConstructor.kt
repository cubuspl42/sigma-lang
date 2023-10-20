package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue

abstract class TypeConstructor : BuiltinFunction() {
    final override val imageType: MembershipType = TypeType

    final override fun apply(argument: Value): Thunk<Value> = Thunk.pure(
        applyType(argument = argument).asValue
    )

    abstract fun applyType(argument: Value): TypeAlike
}
