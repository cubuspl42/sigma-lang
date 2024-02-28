package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.shell.scope.Scope

data class ConstructionContext(
    val scope: Scope,
) {
    companion object {
        val Empty: ConstructionContext = ConstructionContext(
            scope = Scope.Empty,
        )
    }
}
