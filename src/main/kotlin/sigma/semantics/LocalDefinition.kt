package sigma.semantics

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.DefinitionTerm
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value

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

abstract class Definition : Declaration() {
    protected abstract val term: DefinitionTerm

    protected abstract val typeScope: TypeScope

    abstract val meaningExpression: Expression

    private val declaredValueType by lazy {
        term.valueType?.evaluate(
            typeScope = typeScope,
        )
    }

    final override val inferredValueType: Computation<Type> by lazy {
        when (val it = declaredValueType) {
            null -> meaningExpression.inferredType
            else -> Computation.pure(it)
        }
    }
}

class LocalDefinition(
    override val typeScope: TypeScope,
    override val term: DefinitionTerm,
    override val name: Symbol,
    override val meaningExpression: Expression,
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
            meaningExpression = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.value,
            ),
        )
    }

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
