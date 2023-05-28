package sigma.semantics

import sigma.syntax.SourceLocation

interface SemanticError {
    fun dump(): String = toString()

    val location: SourceLocation
}

abstract class Entity {
    abstract val errors: Set<SemanticError>
}
