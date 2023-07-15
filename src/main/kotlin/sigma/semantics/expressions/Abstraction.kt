package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Closure
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.ValueDeclaration
import sigma.semantics.DeclarationBlock
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.TypeScope
import sigma.semantics.types.FunctionType
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.AbstractionTerm

class Abstraction(
    private val innerTypeScope: TypeScope,
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
            outerTypeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            term: AbstractionTerm,
        ): Abstraction {
            val innerTypeScope = term.genericParametersTuple?.toStaticTypeScope(
                typeScope = outerTypeScope,
            ) ?: outerTypeScope

            val argumentType: TupleType = term.argumentType.evaluate(
                typeScope = innerTypeScope,
            )

            val innerDeclarationScope = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = outerDeclarationScope,
            )

            return Abstraction(
                innerTypeScope = innerTypeScope,
                term = term,
                argumentType = argumentType,
                image = build(
                    typeScope = innerTypeScope,
                    declarationScope = innerDeclarationScope,
                    term = term.image,
                ),
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
        term.declaredImageType?.evaluateAsType(
            typeScope = innerTypeScope,
        )
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
