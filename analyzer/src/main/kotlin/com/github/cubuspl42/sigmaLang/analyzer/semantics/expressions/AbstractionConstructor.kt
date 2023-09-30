package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm

class AbstractionConstructor(
    override val outerScope: StaticScope,
    private val innerScope: StaticScope,
    override val term: AbstractionConstructorTerm,
    val genericParameters: Set<TypeVariable>,
    val argumentType: TupleType,
    val declaredImageTypeBody: TypeExpression?,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        override val annotatedType: MembershipType,
    ) : UserDeclaration {
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
            term: AbstractionConstructorTerm,
        ): AbstractionConstructor {
            val genericDeclarationBlock = term.genericParametersTuple?.asDeclarationBlock

            val innerDeclarationScope1 = genericDeclarationBlock?.chainWith(
                outerScope = outerScope,
            ) ?: outerScope

            val argumentTypeBody = TypeExpression.build(
                outerScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.typeOrIllType as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val declaredImageTypeBody = term.declaredImageType?.let {
                TypeExpression.build(
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
                declaredImageTypeBody = declaredImageTypeBody,
                image = image,
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = Closure(
        outerDynamicScope = dynamicScope,
        argumentType = argumentType,
        image = image,
    ).toThunk()

    val declaredImageType = declaredImageTypeBody?.typeOrIllType

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
            directErrors = declaredImageTypeBody?.errors ?: emptySet(),
        )
    }

    override val subExpressions: Set<Expression> = setOfNotNull(image)
}
