package sigma.values

import sigma.syntax.SourceLocation

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
