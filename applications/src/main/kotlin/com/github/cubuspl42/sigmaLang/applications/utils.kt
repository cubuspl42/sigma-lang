package com.github.cubuspl42.sigmaLang.applications

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()
