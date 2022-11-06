package sigma.expressions

sealed class Term {
    abstract val location: SourceLocation
}
