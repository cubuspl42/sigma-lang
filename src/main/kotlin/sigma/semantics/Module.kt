package sigma.semantics

import sigma.evaluation.Thunk
import sigma.syntax.ModuleTerm
import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.DefinitionTerm
import sigma.syntax.StaticStatementTerm

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

    private val staticStatements: Set<StaticStatement> = term.staticStatements.map {
        StaticStatement.build(
            containingModule = this,
            term = it,
        )
    }.toSet()

    val innerDeclarationScope: DeclarationScope = object : DefinitionBlock() {
        override fun getDefinition(
            name: Symbol,
        ): Definition? = this@Module.getGlobalDefinition(name = name)?.asDefinition
    }.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerScope = object : Scope {
        override fun getValue(name: Symbol): Thunk? = this@Module.getGlobalDefinition(name = name)?.definerThunk
    }.chainWith(
        context = prelude.scope,
    )

    fun getGlobalDefinition(name: Symbol): ConstantDefinition? =
        staticStatements.filterIsInstance<ConstantDefinition>().singleOrNull {
            it.asDefinition.name == name
        }

    override val errors: Set<SemanticError> by lazy {
        staticStatements.fold(emptySet()) { acc, it -> acc + it.errors }
    }
}

class ConstantDefinition(
    private val containingModule: Module,
    val term: ConstantDefinitionTerm,
) : StaticStatement() {
    companion object {
        fun build(
            containingModule: Module,
            term: ConstantDefinitionTerm,
        ): ConstantDefinition = ConstantDefinition(
            containingModule = containingModule,
            term = term,
        )
    }

    val asDefinition = object : Definition() {
        override val term: DefinitionTerm
            get() = this@ConstantDefinition.term

        override val name: Symbol = term.name

        override val typeScope: TypeScope = BuiltinTypeScope

        override val definer: Expression by lazy {
            Expression.build(
                typeScope = typeScope,
                declarationScope = containingModule.innerDeclarationScope,
                term = term.definer,
            )
        }
    }

    val definerThunk: Thunk by lazy {
        asDefinition.definer.evaluate(
            scope = containingModule.innerScope,
        )
    }
    override val errors: Set<SemanticError>
        get() = asDefinition.errors
}
