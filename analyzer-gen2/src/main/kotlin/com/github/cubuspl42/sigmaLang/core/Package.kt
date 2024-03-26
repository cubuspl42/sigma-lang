package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.PackageReference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

class Package(
    val path: Path,
    val definitions: Set<Definition>,
) : Element() {
    data class Path(
        val segments: List<Identifier>,
    ) {
        fun resolveElement(
            elementName: Identifier,
        ): Element.Path = Path(
            packagePath = this,
            elementName = elementName,
        )
    }

    data class Definition(
        val name: Identifier,
        val element: Element,
    ) {
        data class Tb(
            val name: Identifier,
            val element: Element.Tb,
        ) {
            fun build(
                packagePath: Path,
            ): Definition = Definition(
                name = name,
                element = element.build(
                    elementPath = packagePath.resolveElement(elementName = name),
                ),
            )
        }
    }

    class Tb(
        private val embody: (PackageReference) -> Body,
    ) : Element.Tb() {
        data class Body(
            val definitions: Set<Definition.Tb>,
        )

        override fun build(
            elementPath: Element.Path,
        ): Package {
            val packagePath = elementPath.toPackagePath()

            return LazyUtils.looped { packageConstructorLooped ->
                val packageReference = PackageReference(
                    referredPackageLazy = packageConstructorLooped,
                )

                val body = embody(packageReference)

                Package(
                    path = packagePath,
                    definitions = body.definitions.mapUniquely {
                        it.build(packagePath = packagePath)
                    },
                )
            }
        }
    }
}
