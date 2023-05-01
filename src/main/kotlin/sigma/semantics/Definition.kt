package sigma.semantics

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.DefinitionTerm
import sigma.values.Symbol
import sigma.values.Value

abstract class Declaration : Entity() {
    abstract val name: Symbol

    abstract val inferredValueType: Computation<Type>
}

class BuiltinDefinition(
    override val name: Symbol,
    val type: Type,
    val value: Value,
) : Declaration() {
    override val inferredValueType: Computation<Type> = Computation.pure(type)

    override val errors: Set<SemanticError> = emptySet()
}

class Definition(
    private val typeScope: TypeScope,
    private val term: DefinitionTerm,
    override val name: Symbol,
    val value: Expression,
) : Declaration() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: DefinitionTerm,
        ): Definition = Definition(
            typeScope = typeScope,
            term = term,
            name = term.name,
            value = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.value,
            ),
        )
    }

    private val declaredValueType = term.valueType?.evaluate(
        typeScope = typeScope,
    )

    override val inferredValueType: Computation<Type> by lazy {
        when (val it = declaredValueType) {
            null -> value.inferredType
            else -> Computation.pure(it)
        }
    }

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
