package sigma.semantics

import sigma.evaluation.Thunk
import sigma.syntax.ModuleTerm
import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type

class Module(
    private val prelude: Prelude,
    private val term: ModuleTerm,
) : Entity() {
    companion object {
        fun build(
            prelude: Prelude,
            term: ModuleTerm,
        ): Module = Module(
            prelude = prelude,
            term = term,
        )
    }

    private val staticDefinitions: Set<StaticDefinition> = term.staticStatements.map {
        StaticDefinition.build(
            containingModule = this,
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

    fun getTypeAliasDefinition(
        name: Symbol,
    ): TypeAliasDefinition? = getStaticDefinition(name = name) as? TypeAliasDefinition

    val innerTypeScope: TypeScope = object : TypeScope {
        override fun getType(typeName: Symbol): Type? = getTypeAliasDefinition(name = typeName)?.aliasedType
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
        override fun getValue(name: Symbol): Thunk? = getConstantDefinition(name = name)?.definerThunk
    }.chainWith(
        context = prelude.scope,
    )

    override val errors: Set<SemanticError> by lazy {
        staticDefinitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }
}
