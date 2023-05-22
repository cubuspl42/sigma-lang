package sigma.semantics

import sigma.BuiltinTypeScope
import sigma.Thunk
import sigma.TypeScope
import sigma.syntax.ModuleTerm
import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.DefinitionTerm

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

    private val globalDefinitions: Set<GlobalDefinition> = term.declarations.map {
        GlobalDefinition.build(
            containingModule = this,
            term = it,
        )
    }.toSet()

    val innerDeclarationScope: DeclarationScope = object : DefinitionBlock() {
        override fun getDefinition(name: Symbol): Definition? = this@Module.getDefinition(name = name)
    }.chainWith(outerScope = prelude.definitionBlock)

    val innerScope = object : Scope {
        override fun getValue(name: Symbol): Thunk? = this@Module.getDefinition(name = name)?.meaningThunk
    }.chainWith(
        context = prelude.scope,
    )

    fun getDefinition(name: Symbol): GlobalDefinition? = globalDefinitions.singleOrNull {
        it.name == name
    }

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}

class GlobalDefinition(
    private val containingModule: Module,
    override val term: DefinitionTerm,
) : Definition() {
    companion object {
        fun build(
            containingModule: Module,
            term: DefinitionTerm,
        ): GlobalDefinition = GlobalDefinition(
            containingModule = containingModule,
            term = term,
        )
    }

    override val name: Symbol = term.name

    override val typeScope: TypeScope = BuiltinTypeScope

    override val meaningExpression: Expression by lazy {
        Expression.build(
            typeScope = typeScope,
            declarationScope = containingModule.innerDeclarationScope,
            term = term.value,
        )
    }

    val meaningThunk: Thunk by lazy {
        meaningExpression.evaluate(
            scope = containingModule.innerScope,
        )
    }

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
