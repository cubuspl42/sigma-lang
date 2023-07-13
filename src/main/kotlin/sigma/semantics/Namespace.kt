package sigma.semantics

import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.types.Type
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

    val innerTypeScope: TypeScope = object : TypeScope {
        override fun getType(typeName: Symbol): Type? = getStaticDefinition(name = typeName)?.definedType
    }.chainWith(
        backScope = BuiltinTypeScope,
    )

    val innerDeclarationScope: DeclarationScope = object : DefinitionBlock() {
        override fun getDefinition(
            name: Symbol,
        ): ValueDefinition? = getConstantDefinition(name = name)?.asValueDefinition
    }.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerScope = object : Scope {
        override fun getValue(name: Symbol): Value? = getStaticDefinition(name = name)?.definedValue
    }.chainWith(
        context = prelude.scope,
    )

    val errors: Set<SemanticError> by lazy {
        staticDefinitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    override fun dump(): String = "(namespace)"
}
