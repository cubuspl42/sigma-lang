package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AbstractionConstructor : Expression() {
    abstract val metaArgumentType: TupleType?

    abstract val argumentType: TupleType

    abstract val argumentDeclarationBlock: ArgumentStaticBlock

    abstract val declaredImageTypeBody: TypeExpression?

    abstract val image: Expression

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
            context: BuildContext,
            term: AbstractionConstructorTerm,
        ): Stub<AbstractionConstructor> = object : Stub<AbstractionConstructor> {
            override val resolved: AbstractionConstructor by lazy {
                val outerMetaScope = context.outerMetaScope
                val outerScope = context.outerScope

                val metaArgumentTypeConstructor = term.metaArgumentType?.let {
                    TypeExpression.build(
                        outerMetaScope = outerScope,
                        term = it,
                    ).resolved
                }

                val metaArgumentType = metaArgumentTypeConstructor?.typeOrIllType?.let { it as TupleType }

                val metaArgumentBlock = metaArgumentType?.toMetaArgumentDeclarationBlock()

                val innerMetaScope = metaArgumentBlock.chainWithIfNotNull(
                    outerScope = outerMetaScope,
                )

                val argumentTypeBody = TypeExpression.build(
                    outerMetaScope = innerMetaScope,
                    term = term.argumentType,
                ).resolved

                val argumentType = argumentTypeBody.typeOrIllType as TupleType

                val argumentDeclarationBlock = argumentType.toArgumentDeclarationBlock()

                val innerScope = argumentDeclarationBlock.chainWith(
                    outerScope = outerScope,
                )

                val declaredImageTypeBody = term.declaredImageType?.let {
                    TypeExpression.build(
                        outerMetaScope = innerMetaScope,
                        term = it,
                    ).resolved
                }

                val image = build(
                    context = BuildContext(
                        outerMetaScope = innerMetaScope,
                        outerScope = innerScope,
                    ),
                    term = term.image,
                )

                return@lazy object : AbstractionConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: ExpressionTerm = term

                    override val metaArgumentType = metaArgumentType

                    override val argumentType = argumentType

                    override val argumentDeclarationBlock = argumentDeclarationBlock

                    override val declaredImageTypeBody = declaredImageTypeBody

                    override val image by lazy { image.resolved }
                }
            }
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = ComputableAbstraction(
        outerDynamicScope = dynamicScope,
        argumentType = argumentType,
        image = image,
    ).toThunk()

    val declaredImageType: MembershipType?
        get() = declaredImageTypeBody?.typeOrIllType

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

    override val subExpressions: Set<Expression> by lazy { setOfNotNull(image) }
}
