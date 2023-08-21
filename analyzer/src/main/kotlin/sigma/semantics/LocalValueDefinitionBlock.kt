package sigma.semantics

import sigma.evaluation.scope.LoopedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Symbol
import sigma.syntax.LocalDefinitionSourceTerm

class LocalValueDefinitionBlock(
    private val declarationScope: StaticScope,
    private val declarations: List<LocalDefinitionSourceTerm>,
) : StaticBlock() {
    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            definitions: List<LocalDefinitionSourceTerm>,
        ): LocalValueDefinitionBlock = LocalValueDefinitionBlock(
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName by lazy {
        declarations.associate {
            it.name to LocalValueDefinition.build(
                declarationScope = declarationScope,
                term = it,
            )
        }
    }

    fun getValueDefinition(name: Symbol): LocalValueDefinition? = definitionByName[name]

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedName? = getValueDefinition(name = name)?.let {
        ResolvedName(
            type = it.effectiveValueType,
            resolution = DynamicResolution(
                resolvedFormula = null,
            )
        )
    }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        scope: Scope,
    ): Scope = LoopedScope(
        outerScope = scope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.body
        },
    )
}
