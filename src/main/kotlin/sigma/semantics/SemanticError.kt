package sigma.semantics

import sigma.syntax.SourceLocation

interface SemanticError {
    fun dump(): String = toString()

    val location: SourceLocation
}
