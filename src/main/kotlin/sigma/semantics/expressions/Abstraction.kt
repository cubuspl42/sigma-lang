package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Closure
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.DeclarationBlock
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.ValueDeclaration
import sigma.semantics.types.FunctionType
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.AbstractionTerm

class Abstraction(
    private val innerDeclarationScope: StaticScope,
    override val term: AbstractionTerm,
    val argumentType: TupleType,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val type: Type,
    ) : ValueDeclaration {
        override val effectiveValueType: Computation<Type> = Computation.pure(type)
    }

    class ArgumentDeclarationBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : DeclarationBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun getDeclaration(name: Symbol): ValueDeclaration? = declarationByName[name]
    }

    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            term: AbstractionTerm,
        ): Abstraction {
            val genericDeclarationBlock = term.genericParametersTuple?.asDeclarationBlock

            val innerDeclarationScope1 = genericDeclarationBlock?.chainWith(
                outerScope = outerDeclarationScope,
            ) ?: outerDeclarationScope


            val argumentTypeBody =  build(
                declarationScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            // TODO
            val argumentType = argumentTypeBody.evaluate(scope = Scope.Empty) as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val image = build(
                declarationScope = innerDeclarationScope2,
                term = term.image,
            )

            return Abstraction(
                innerDeclarationScope = innerDeclarationScope2,
                term = term,
                argumentType = argumentType,
                image = image,
            )
        }
    }

    override fun evaluate(
        scope: Scope,
    ): Value = Closure(
        context = scope,
        argumentType = argumentType,
        image = image,
    )

    private val declaredImageType: Type? by lazy {
        TODO()
    }

    override val inferredType: Computation<FunctionType> by lazy {
        inferredImageType.thenJust { inferredImageType ->
            UniversalFunctionType(
                argumentType = argumentType,
                imageType = inferredImageType,
            )
        }
    }

    private val inferredImageType: Computation<Type> by lazy {
        when (val it = declaredImageType) {
            null -> image.inferredType
            else -> Computation.pure(it)
        }
    }

    override val errors: Set<SemanticError> by lazy {
        image.errors
    }
}
