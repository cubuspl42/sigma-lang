package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.ExpressedAbstraction
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
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

    val rootAbstractionConstructor = module.root as AbstractionConstructor

    val rootAbstraction = rootAbstractionConstructor.bind(scope = DynamicScope.Empty).value as ExpressedAbstraction

    val result = rootAbstraction.call(
        argument = UnorderedTuple.Empty,
    )

    println(result)

    val codeGenRootAbstraction = Out.root.value as Abstraction

    val codeGenResult = codeGenRootAbstraction.compute(argument = UnorderedTuple.Empty)

    println(codeGenResult)
}
