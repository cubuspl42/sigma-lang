package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope

data class ConstructionContext(
    val scope: StaticScope,
) {
    companion object {
        val Empty: ConstructionContext = ConstructionContext(
            scope = StaticScope.Empty,
        )
    }
}
