package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionTerm

class AbstractionConstructor(
    override val outerScope: StaticScope,
    private val innerScope: StaticScope,
    override val term: AbstractionTerm,
    val genericParameters: Set<TypeVariable>,
    val argumentType: TupleType,
    val declaredImageTypeConstructor: Expression?,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val annotatedType: MembershipType,
    ) : UserDeclaration {
        override val annotatedTypeThunk = Thunk.pure(annotatedType)

        override val errors: Set<SemanticError> = emptySet()
    }

    class ArgumentStaticBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): ClassifiedIntroduction? = declarationByName[name]

        override fun getLocalNames(): Set<Symbol> = declarationByName.keys
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: AbstractionTerm,
        ): AbstractionConstructor {
            val genericDeclarationBlock = term.genericParametersTuple?.asDeclarationBlock

            val innerDeclarationScope1 = genericDeclarationBlock?.chainWith(
                outerScope = outerScope,
            ) ?: outerScope

            val argumentTypeBody = TupleTypeConstructor.build(
                outerScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = TranslationDynamicScope(
                    staticScope = innerDeclarationScope1,
                ),
            )?.asType as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val declaredImageType = term.declaredImageType?.let {
                Expression.build(
                    outerScope = innerDeclarationScope2,
                    term = it,
                )
            }

            val image = build(
                outerScope = innerDeclarationScope2,
                term = term.image,
            )

            return AbstractionConstructor(
                outerScope = outerScope,
                innerScope = innerDeclarationScope2,
                term = term,
                genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
                argumentType = argumentType,
                declaredImageTypeConstructor = declaredImageType,
                image = image,
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = Closure(
        outerDynamicScope = dynamicScope,
        argumentType = argumentType,
        image = image,
    ).toThunk()

    private val declaredImageTypeThunk: Thunk<MembershipType>? by lazy {
        declaredImageTypeConstructor?.bindTranslated(
            staticScope = innerScope,
        )?.thenJust { it.asType!! }
    }

    val declaredImageType = declaredImageTypeThunk?.let { it.value ?: IllType }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val effectiveImageType = this@AbstractionConstructor.declaredImageType ?: run {
            compute(image.inferredTypeOrIllType)
        }

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = UniversalFunctionType(
                    genericParameters = genericParameters,
                    argumentType = argumentType,
                    imageType = effectiveImageType,
                )
            ),
            directErrors = emptySet(),
        )
    }

    override val subExpressions: Set<Expression> = setOfNotNull(declaredImageTypeConstructor, image)
}
