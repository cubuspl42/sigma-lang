package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.FunctionType
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.AbstractionTerm

class Abstraction(
    private val outerTypeScope: TypeScope,
    override val term: AbstractionTerm,
    val image: Expression,
) : Expression() {
    companion object {
        fun build(
            outerTypeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: AbstractionTerm,
        ): Abstraction = Abstraction(
            outerTypeScope = outerTypeScope,
            term = term,
            image = build(
                typeScope = outerTypeScope,
                declarationScope = declarationScope,
                term = term.image,
            ),
        )
    }

    private val genericParametersTuple: AbstractionTerm.GenericParametersTuple?
        get() = term.genericParametersTuple

    private val innerTypeScope: TypeScope = genericParametersTuple?.toStaticTypeScope()?.chainWith(
        backScope = outerTypeScope,
    ) ?: outerTypeScope

    val argumentType: TupleType = term.argumentType.evaluate(
        typeScope = innerTypeScope,
    )

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
