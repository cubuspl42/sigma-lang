package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.ModuleLoader
import com.github.cubuspl42.sigmaLang.shell.ProjectLoader
import java.nio.file.Path

fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

fun main() {
    val projectConstructor = ProjectLoader(
        moduleLoader = ModuleLoader(
            class_ = object {}.javaClass,
        ),
    ).loadProject(
        mainModulePath = ModulePath(
            name = Identifier(name = "main"),
        )
    ).build()

    val fileSpec = projectConstructor.generateCode(
        packageName = "com.github.cubuspl42.sigmaLang",
        name = "Out",
    )

    fileSpec.writeTo(
        directory = Path.of("analyzer-gen2/src/main/kotlin"),
    )

    val result = projectConstructor.rawExpression.bind(
        scope = DynamicScope.Bottom,
    )

    println(result)

    val codeGenResult = Out.root

    println(codeGenResult)
}
