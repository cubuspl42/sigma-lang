package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.DefinitionTerm

class ConstantDefinition(
    private val containingNamespace: Namespace,
    val term: ConstantDefinitionTerm,
) : StaticDefinition() {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: ConstantDefinitionTerm,
        ): ConstantDefinition = ConstantDefinition(
            containingNamespace = containingNamespace,
            term = term,
        )
    }

    inner class ConstantValueDefinition : ValueDefinition() {
        override val term: DefinitionTerm
            get() = this@ConstantDefinition.term

        override val name: Symbol
            get() = this@ConstantDefinition.name

        override val typeScope: TypeScope
            get() = containingNamespace.innerTypeScope

        override val definer: Expression by lazy {
            Expression.build(
                typeScope = typeScope,
                declarationScope = containingNamespace.innerDeclarationScope,
                term = term.definer,
            )
        }
    }

    val asValueDefinition = ConstantValueDefinition()

    val definedValue: Value by lazy {
        asValueDefinition.definer.evaluate(
            scope = containingNamespace.innerScope,
        )
    }

    override val name: Symbol
        get() = term.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}
