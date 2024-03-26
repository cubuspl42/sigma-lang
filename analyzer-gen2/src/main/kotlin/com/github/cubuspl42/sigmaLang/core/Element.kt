package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.values.Identifier

sealed class Element : CoreTerm() {
    data class Path(
        val packagePath: Package.Path,
        val elementName: Identifier,
    ) {

        fun toPackagePath(): Package.Path = Package.Path(
            segments = packagePath.segments + elementName,
        )
    }

    sealed class Tb {
        abstract fun build(
            elementPath: Path,
        ): Element
    }
}
