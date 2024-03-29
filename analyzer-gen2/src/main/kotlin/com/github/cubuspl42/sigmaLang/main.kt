package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.IndexableValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.ModuleLoader
import com.github.cubuspl42.sigmaLang.shell.ProjectLoader

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

//    val fileSpec = projectConstructor.generateCode(
//        packageName = "com.github.cubuspl42.sigmaLang",
//        name = "Out",
//    )

//    fileSpec.writeTo(
//        directory = Path.of("analyzer-gen2/src/main/kotlin"),
//    )

    val root = projectConstructor.evaluate()

    projectConstructor.evaluate()

    val result = extractMain(root)

    println(result)

//    val codeGenRoot = Out.root
//
//    val codeGenResult = extractMain(codeGenRoot)
//
//    println(codeGenResult)
}

private fun extractMain(root: Value): Value {
    val main = Identifier(name = "main")
    return ((root as IndexableValue).get(main) as IndexableValue).get(main)
}
