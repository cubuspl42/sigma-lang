package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType

abstract class WrapperTypeConstructor : TypeConstructor() {
    override val argumentType: TableType = ArrayType(elementType = TypeType)

    override fun applyType(
        argument: Value,
    ): MembershipType = wrapType(
        wrappedType = (argument as TypeValue<*>).asType,
    )

    abstract fun wrapType(wrappedType: MembershipType): MembershipType
}
