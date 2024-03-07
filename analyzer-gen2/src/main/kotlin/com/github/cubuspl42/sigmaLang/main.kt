package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm
import java.nio.file.Path

fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

fun main() {
    val source = getResourceAsText("main.sigma") ?: throw IllegalArgumentException("No source found")

    val moduleTerm = ModuleTerm.parse(source = source)

    val moduleConstructor = moduleTerm.build()

    val fileSpec = moduleConstructor.generateCode(
        packageName = "com.github.cubuspl42.sigmaLang",
        name = "Out",
    )

    fileSpec.writeTo(
        directory = Path.of("analyzer-gen2/src/main/kotlin"),
    )

    val result = moduleConstructor.main

    println(result)

    val codeGenResult = Out.main

    println(codeGenResult)
}
