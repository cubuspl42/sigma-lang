package sigma.semantics

import sigma.syntax.SourceLocation

interface SemanticError {
    val location: SourceLocation
}

abstract class Entity {
    abstract val errors: Set<SemanticError>
}
