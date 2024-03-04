package com.github.cubuspl42.sigmaLang.utils

import com.squareup.kotlinpoet.CodeBlock

fun CodeBlock.wrapWithLazyOf(): CodeBlock = CodeBlock.of(
    """
        lazyOf(
        ⇥%L,
        ⇤)
    """.trimIndent(),
    this,
)
