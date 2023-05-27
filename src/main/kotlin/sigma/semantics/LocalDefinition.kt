package sigma.semantics

import sigma.Computation
import sigma.TypeScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.DefinitionTerm

abstract class Declaration : Entity() {
    abstract val name: Symbol

    abstract val inferredType: Computation<Type>
}

class BuiltinDefinition(
    override val name: Symbol,
    val type: Type,
    val value: Value,
) : Declaration() {
    override val inferredType: Computation<Type> = Computation.pure(type)

    override val errors: Set<SemanticError> = emptySet()
}

abstract class Definition : Declaration() {
    protected abstract val term: DefinitionTerm

    protected abstract val typeScope: TypeScope

    abstract val definer: Expression

    private val declaredValueType by lazy {
        term.valueType?.evaluate(
            typeScope = typeScope,
        )
    }

    final override val inferredType: Computation<Type> by lazy {
        when (val it = declaredValueType) {
            null -> definer.inferredType
            else -> Computation.pure(it)
        }
    }
}

class LocalDefinition(
    override val typeScope: TypeScope,
    override val term: DefinitionTerm,
    override val name: Symbol,
    override val definer: Expression,
) : Definition() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: DefinitionTerm,
        ): LocalDefinition = LocalDefinition(
            typeScope = typeScope,
            term = term,
            name = term.name,
            definer = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.value,
            ),
        )
    }

    // TODO: Check if the declared type matches the inferred one
    override val errors: Set<SemanticError> by lazy { definer.errors }
}
