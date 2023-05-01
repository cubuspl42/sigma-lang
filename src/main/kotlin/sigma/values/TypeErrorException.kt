package sigma.values

import sigma.syntax.SourceLocation

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
