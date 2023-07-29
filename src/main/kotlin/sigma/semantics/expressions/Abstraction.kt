package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Closure
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateValueHacky
import sigma.semantics.DynamicResolution
import sigma.semantics.Formula
import sigma.semantics.ResolvedName
import sigma.semantics.SemanticError
import sigma.semantics.StaticBlock
import sigma.semantics.StaticScope
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
    val declaredImageTypeConstructor: Expression?,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val type: Type,
    ) : ValueDeclaration {
        override val effectiveValueType: Thunk<Type> = Thunk.pure(type)
    }

    class ArgumentStaticBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = declarationByName[name]?.let {
            ResolvedName(
                type = it.type.asThunk,
                resolution = DynamicResolution(
                    resolvedFormula = Formula(
                        name = name,
                    ),
                ),
            )
        }
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

            val argumentTypeBody = TupleTypeConstructor.build(
                declarationScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.evaluateValue(
                context = EvaluationContext.Initial,
                scope = TranslationScope(
                    staticScope = innerDeclarationScope1,
                ),
            ) as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val declaredImageType = term.declaredImageType?.let {
                Expression.build(
                    declarationScope = innerDeclarationScope2,
                    term = it,
                )
            }

            val image = build(
                declarationScope = innerDeclarationScope2,
                term = term.image,
            )

            return Abstraction(
                innerDeclarationScope = innerDeclarationScope2,
                term = term,
                argumentType = argumentType,
                declaredImageTypeConstructor = declaredImageType,
                image = image,
            )
        }
    }
    override fun bind(scope: Scope): Thunk<Value> = Closure(
        outerScope = scope,
        argumentType = argumentType,
        image = image,
    ).asThunk

    val declaredImageType: Thunk<Type>? by lazy {
        declaredImageTypeConstructor?.bindTranslated(
            staticScope = innerDeclarationScope,
        )?.thenJust { it as Type }
    }

    private val effectiveImageType: Thunk<Type> by lazy {
        val declaredImageType = this.declaredImageType

        if (declaredImageType != null) {
            return@lazy declaredImageType
        } else {
            return@lazy image.inferredType
        }
    }

    override val inferredType: Thunk<FunctionType> by lazy {
        effectiveImageType.thenJust { effectiveImageType ->
            UniversalFunctionType(
                argumentType = argumentType,
                imageType = effectiveImageType,
            )
        }
    }

    override val errors: Set<SemanticError> by lazy {
        image.errors
    }
}
