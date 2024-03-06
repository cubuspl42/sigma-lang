package com.github.cubuspl42.sigmaLang

import java.nio.file.Path

fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

fun main() {
    val source = getResourceAsText("main.sigma") ?: throw IllegalArgumentException("No source found")

    val module = Module.fromSource(source = source)

    val fileSpec = module.generateCode(
        packageName = "com.github.cubuspl42.sigmaLang",
        name = "Out",
    )

    fileSpec.writeTo(
        directory = Path.of("analyzer-gen2/src/main/kotlin"),
    )

    val result = module.main

    println(result)

    val codeGenResult = Out.main

    println(codeGenResult)
}

