package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
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
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm

class AbstractionConstructor(
    override val outerScope: StaticScope,
    private val innerScope: StaticScope,
    override val term: AbstractionConstructorTerm,
    val metaArgumentType: TupleType?,
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
        val argumentDeclarations: Set<ArgumentDeclaration>,
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
            val metaArgumentTypeConstructor = term.metaArgumentType?.let {
                TypeExpression.build(
                    outerScope = outerScope,
                    term = it,
                )
            }

            val metaArgumentType = metaArgumentTypeConstructor?.typeOrIllType?.let { it as TupleType }

            val metaArgumentBlock = metaArgumentType?.toMetaArgumentDeclarationBlock()

            val innerDeclarationScope1 = metaArgumentBlock?.chainWith(
                outerScope = outerScope,
            ) ?: outerScope

            val argumentTypeBody = TypeExpression.build(
                outerScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.typeOrIllType as TupleType

            val argumentDeclarationBlock = argumentType.toArgumentDeclarationBlock()

            val innerDeclarationScope2 = argumentDeclarationBlock.chainWith(
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
                metaArgumentType = metaArgumentType,
                argumentType = argumentType,
                declaredImageTypeBody = declaredImageTypeBody,
                image = image,
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = ComputableAbstraction(
        outerDynamicScope = dynamicScope,
        argumentType = argumentType,
        image = image,
    ).toThunk()

    private val argumentDeclarationBlock = argumentType.toArgumentDeclarationBlock()

    val declaredImageType = declaredImageTypeBody?.typeOrIllType

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = object : Analysis() {
                private val imageAnalysis by lazy {
                    compute(image.computedAnalysis)
                }

                override val inferredType = run {
                    val effectiveImageType = this@AbstractionConstructor.declaredImageType ?: run {
                        imageAnalysis?.inferredType ?: IllType
                    }

                    UniversalFunctionType(
                        metaArgumentType = metaArgumentType,
                        argumentType = argumentType,
                        imageType = effectiveImageType,
                    )
                }
            },
            directErrors = declaredImageTypeBody?.errors ?: emptySet(),
        )
    }


    override val classifiedValue by lazy {
        // TODO: Support proper const-classification of recursive abstractions
        when (val classifiedImageValue = this.image.classifiedValue) {
            is ConstClassificationContext -> classifiedImageValue.transform {
                ProviderAbstraction(result = it)
            }

            is VariableClassificationContext -> classifiedImageValue.withResolvedDeclarations(
                declarations = argumentDeclarationBlock.argumentDeclarations,
                buildConst = {
                    Thunk.pure(
                        ComputableAbstraction(
                            outerDynamicScope = DynamicScope.Empty,
                            argumentType = argumentType,
                            image = image,
                        )
                    )
                },
                buildVariable = { dynamicScope ->
                    Thunk.pure(
                        ComputableAbstraction(
                            outerDynamicScope = dynamicScope,
                            argumentType = argumentType,
                            image = image,
                        )
                    )
                },
            )
        }
    }

    override val subExpressions: Set<Expression> = setOfNotNull(image)
}
