package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType

abstract class BuiltinFunction : FunctionValue(), BuiltinValue {
    final override val type: MembershipType
        get() = UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )

    final override val value: Value
        get() = this

    // Thought: allow for names
    abstract val argumentType: TableType

    abstract val imageType: MembershipType
}
