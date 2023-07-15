package sigma.semantics

import sigma.evaluation.scope.LoopedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Symbol
import sigma.syntax.LocalDefinitionTerm

class LocalValueDefinitionBlock(
    private val typeScope: TypeScope,
    private val declarationScope: DeclarationScope,
    private val declarations: List<LocalDefinitionTerm>,
) : DeclarationBlock() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            definitions: List<LocalDefinitionTerm>,
        ): LocalValueDefinitionBlock = LocalValueDefinitionBlock(
            typeScope = typeScope,
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName = declarations.associate {
        it.name to LocalValueDefinition.build(
            typeScope = typeScope,
            declarationScope = declarationScope,
            term = it,
        )
    }

    fun getValueDefinition(name: Symbol): LocalValueDefinition? = definitionByName[name]

    override fun getDeclaration(
        name: Symbol,
    ): Declaration? = getValueDefinition(name = name)

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        scope: Scope,
    ): Scope = LoopedScope(
        context = scope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.definer
        },
    )
}
