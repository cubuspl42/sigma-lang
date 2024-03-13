package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

data class FormationContext(
    val scope: StaticScope,
) {
    fun extendScope(innerScope: StaticScope): FormationContext = copy(
        scope = innerScope.chainWith(scope),
    )
}
