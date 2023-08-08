package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression
import sigma.syntax.ConstantDefinitionTerm

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
        override val name: Symbol
            get() = this@ConstantDefinition.name

        override val declarationScope: StaticScope
            get() = containingNamespace.innerDeclarationScope

        override val declaredTypeBody: Expression? by lazy {
            term.declaredTypeBody?.let {
                Expression.build(
                    declarationScope = declarationScope,
                    term = it,
                )
            }
        }

        override val body: Expression by lazy {
            Expression.build(
                declarationScope = containingNamespace.innerDeclarationScope,
                term = term.body,
            )
        }
    }

    val asValueDefinition = ConstantValueDefinition()


    val valueThunk by lazy {
        asValueDefinition.body.bind(
            scope = containingNamespace.innerScope,
        )
    }

    override val staticValue: Thunk<Value>
        get() = this.valueThunk

    override val name: Symbol
        get() = term.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}
