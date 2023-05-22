package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.Declaration
import sigma.semantics.DeclarationBlock
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.FunctionType
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.AbstractionTerm
import sigma.evaluation.values.Symbol

class Abstraction(
    private val outerTypeScope: TypeScope,
    override val term: AbstractionTerm,
    val argumentType: TupleType,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val type: Type,
    ) : Declaration() {
        override val inferredValueType: Computation<Type> = Computation.pure(type)

        override val errors: Set<SemanticError> = emptySet()
    }

    class ArgumentDeclarationBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : DeclarationBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun getDeclaration(name: Symbol): Declaration? = declarationByName[name]
    }

    companion object {
        fun build(
            outerTypeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            term: AbstractionTerm,
        ): Abstraction {
            val innerTypeScope: TypeScope = term.genericParametersTuple?.toStaticTypeScope()?.chainWith(
                backScope = outerTypeScope,
            ) ?: outerTypeScope

            val argumentType: TupleType = term.argumentType.evaluate(
                typeScope = innerTypeScope,
            )

            val innerDeclarationScope = DeclarationScope.Chained(
                outerScope = outerDeclarationScope,
                declarationBlock = argumentType.toArgumentDeclarationBlock(),
            )

            return Abstraction(
                outerTypeScope = outerTypeScope,
                term = term,
                argumentType = argumentType,
                image = build(
                    typeScope = outerTypeScope,
                    declarationScope = innerDeclarationScope,
                    term = term.image,
                ),
            )
        }
    }

    private val declaredImageType: Type? by lazy {
        term.declaredImageType?.evaluate(
            typeScope = outerTypeScope,
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

    override val errors: Set<SemanticError> = emptySet()
}
