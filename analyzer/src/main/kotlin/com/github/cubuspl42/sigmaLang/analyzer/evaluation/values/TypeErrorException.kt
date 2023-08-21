package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

open class TypeErrorException(
    location: SourceLocation? = null,
    message: String,
) : Exception(
    listOfNotNull(
        location?.toString(),
        message,
    ).joinToString(
        separator = " ",
    )
)
