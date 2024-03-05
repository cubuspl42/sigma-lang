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

fun CodeBlock.wrapWithLazy(): CodeBlock = CodeBlock.of(
    """
        lazy {
        ⇥%L 
        ⇤}
    """.trimIndent(),
    this,
)

fun CodeBlock.wrapWithLazier(): CodeBlock = CodeBlock.of(
    """
        %T.lazier {
        ⇥%L 
        ⇤}
    """.trimIndent(),
    LazyUtils::class,
    this,
)
