package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.DefinitionTerm

class ConstantDefinition(
    private val containingModule: Module,
    val term: ConstantDefinitionTerm,
) : StaticDefinition() {
    companion object {
        fun build(
            containingModule: Module,
            term: ConstantDefinitionTerm,
        ): ConstantDefinition = ConstantDefinition(
            containingModule = containingModule,
            term = term,
        )
    }

    val asValueDefinition = object : ValueDefinition() {
        override val term: DefinitionTerm
            get() = this@ConstantDefinition.term

        override val name: Symbol = term.name

        override val typeScope: TypeScope
            get() = containingModule.innerTypeScope

        override val definer: Expression by lazy {
            Expression.build(
                typeScope = typeScope,
                declarationScope = containingModule.innerDeclarationScope,
                term = term.definer,
            )
        }
    }

    override val definedValue: Value by lazy {
        asValueDefinition.definer.evaluate(
            scope = containingModule.innerScope,
        )
    }

    override val name: Symbol
        get() = asValueDefinition.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}
