package sigma.values

import sigma.expressions.SourceLocation

open class TypeError(
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
