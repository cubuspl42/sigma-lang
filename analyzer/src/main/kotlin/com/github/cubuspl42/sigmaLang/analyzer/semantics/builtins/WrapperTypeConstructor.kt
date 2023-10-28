package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType

abstract class WrapperTypeConstructor : TypeConstructorConstructor() {
    override val argumentType: TableType = ArrayType(elementType = TypeType)

    override fun applyType(
        argument: Value,
    ): TypeAlike = wrapType(
        wrappedType = (argument as TypeValue<*>).asType,
    )

    abstract fun wrapType(wrappedType: TypeAlike): SpecificType
}
