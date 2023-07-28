package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Closure
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.semantics.Computation
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
        override val effectiveValueType: Computation<Type> = Computation.pure(type)
    }

    class ArgumentStaticBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = declarationByName[name]?.let {
            ResolvedName(
                type = it.type,
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

    override fun evaluateDirectly(
        context: EvaluationContext,
        scope: Scope,
    ): EvaluationResult = Closure(
        outerScope = scope,
        argumentType = argumentType,
        image = image,
    ).asEvaluationResult

    val declaredImageType by lazy {
        declaredImageTypeConstructor?.bindTranslated(
            staticScope = innerDeclarationScope,
        )?.evaluateValue(
            context = EvaluationContext.Initial,
        ) as? Type
    }

    private val effectiveImageType: Computation<Type> by lazy {
        val declaredImageType = this.declaredImageType

        if (declaredImageType != null) {
            return@lazy Computation.pure(declaredImageType)
        } else {
            return@lazy image.inferredType
        }
    }

    override val inferredType: Computation<FunctionType> by lazy {
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
