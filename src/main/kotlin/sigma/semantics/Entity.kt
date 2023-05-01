package sigma.semantics

interface SemanticError

abstract class Entity {
    abstract val errors: Set<SemanticError>
}
