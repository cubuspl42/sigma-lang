package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.semantics.types.Type
import sigma.syntax.ClassDefinitionTerm

class ClassDefinition(
    private val containingNamespace: Namespace,
    private val term: ClassDefinitionTerm,
) : StaticDefinition() {
    inner class MethodDefinition(
        val name: Symbol,
        val body: Expression,
    )

    companion object {
        fun build(): ClassDefinition {
            TODO()
        }
    }

    override val name: Symbol
        get() = term.name

    val methodDefinitions: Set<MethodDefinition> = term.methodDefinitions.map {
        MethodDefinition(
            name = it.name,
            body = Expression.build(
                typeScope = BuiltinTypeScope,
                declarationScope = containingNamespace.innerDeclarationScope,
                term = it.body,
            ),
        )
    }.toSet()

    override val definedValue: Value?
        get() = super.definedValue

    override val definedType: Type?
        get() = super.definedType

    override val errors: Set<SemanticError> = emptySet()
}
