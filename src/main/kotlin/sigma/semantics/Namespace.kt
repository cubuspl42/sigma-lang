package sigma.semantics

import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.EvaluationContext
import sigma.syntax.NamespaceDefinitionTerm

class Namespace(
    private val prelude: Prelude,
    private val term: NamespaceDefinitionTerm,
) : Value() {
    companion object {
        fun build(
            prelude: Prelude,
            term: NamespaceDefinitionTerm,
        ): Namespace = Namespace(
            prelude = prelude,
            term = term,
        )
    }

    private val staticDefinitions: Set<StaticDefinition> = term.staticStatements.map {
        StaticDefinition.build(
            containingNamespace = this,
            term = it,
        )
    }.toSet()

    private fun getStaticDefinition(
        name: Symbol,
    ): StaticDefinition? = staticDefinitions.singleOrNull {
        it.name == name
    }

    fun getConstantDefinition(
        name: Symbol,
    ): ConstantDefinition? = getStaticDefinition(name = name) as? ConstantDefinition

    inner class NamespaceDeclarationBlock : DeclarationBlock() {
        override fun getDeclaration(
            name: Symbol,
        ): Declaration? = getStaticDefinition(name = name)
    }

    private val asDeclarationBlock = NamespaceDeclarationBlock()

    val innerDeclarationScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerScope = object : Scope {
        override fun getValue(
            context: EvaluationContext,
            name: Symbol,
        ): EvaluationResult? {
            val constantDefinition = getStaticDefinition(name = name) as? ConstantDefinition
            return constantDefinition?.definedValue
        }
    }.chainWith(
        context = prelude.scope,
    )

    val errors: Set<SemanticError> by lazy {
        staticDefinitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    override fun dump(): String = "(namespace)"
}
