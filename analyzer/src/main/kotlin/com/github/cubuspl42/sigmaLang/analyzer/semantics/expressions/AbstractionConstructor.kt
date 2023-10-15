package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassifiedExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AbstractionConstructor : Expression() {
    abstract val metaArgumentType: TupleType?

    abstract val argumentType: TupleType

    // TODO: This is a terrible hack
    open val argumentDeclarationBlock by lazy { argumentType.toArgumentDeclarationBlock() }

//    abstract val argumentDeclarationBlock: ArgumentStaticBlock

    abstract val declaredImageType: MembershipType?

    abstract val image: Expression

    class ArgumentDeclaration(
        override val name: Symbol,
        override val annotatedType: MembershipType,
    ) : Declaration

    class ArgumentStaticBlock(
        val argumentDeclarations: Set<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): Introduction? = declarationByName[name]

        override fun getLocalNames(): Set<Symbol> = declarationByName.keys
    }

    companion object {
        fun build(
            context: BuildContext,
            term: AbstractionConstructorTerm,
        ): Stub<AbstractionConstructor> {
            val outerMetaScope = context.outerMetaScope

            val outerScope = context.outerScope

            val metaArgumentTypeConstructorStub = term.metaArgumentType?.let {
                TypeExpression.build(
                    outerMetaScope = outerScope,
                    term = it,
                )
            }

            val argumentTypeConstructorStub = term.argumentType.let {
                Expression.build(
                    context = context,
                    term = it,
                )
            }

            return object : Stub<AbstractionConstructor> {
                override val resolved: AbstractionConstructor by lazy {
                    val metaArgumentTypeConstructor = metaArgumentTypeConstructorStub?.resolved
                    val argumentTypeConstructor = argumentTypeConstructorStub.resolved

                    // These scopes might be messed up
                    val metaArgumentTypeConstructorAnalysis = metaArgumentTypeConstructor?.analyzeAsType(
                        outerScope = outerScope,
                    )

                    val metaArgumentType = metaArgumentTypeConstructorAnalysis?.typeOrIllType?.let { it as TupleType }


                    val metaArgumentBlock = metaArgumentType?.toMetaArgumentDeclarationBlock()

                    val innerMetaScope = metaArgumentBlock.chainWithIfNotNull(
                        outerScope = outerMetaScope,
                    )

                    val argumentTypeBodyAnalysis = argumentTypeConstructor.analyzeAsType(
                        outerScope = outerScope,
                    )

                    val argumentType = argumentTypeBodyAnalysis.typeOrIllType as TupleType

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

                    val declaredImageTypeAnalysis = declaredImageTypeBody?.analyzeAsType(
                        outerScope = outerScope,
                    )

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

                        override val declaredImageType: MembershipType? = declaredImageTypeAnalysis?.typeOrIllType

                        override val image by lazy { image.resolved }
                    }
                }
            }
        }

        fun build2(
            outerScope: StaticScope,
            term: ExpressionTerm?,
            metaArgumentType: TupleType?,
            argumentType: TupleType,
            declaredImageType: MembershipType?,
            imageConstructor: Expression,
        ): Stub<AbstractionConstructor> = object : Stub<AbstractionConstructor> {
            override val resolved: AbstractionConstructor by lazy {
                return@lazy object : AbstractionConstructor() {
                    override val term: ExpressionTerm? = term

                    override val outerScope: StaticScope = outerScope

                    override val metaArgumentType = metaArgumentType

                    override val argumentType = argumentType

//                    override val argumentDeclarationBlock = argumentDeclarationBlock

                    override val declaredImageType: MembershipType? = declaredImageType

                    override val image = imageConstructor
                }
            }
        }
    }

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(
        ComputableAbstraction(
            outerDynamicScope = dynamicScope,
            argumentType = argumentType,
            image = image,
        ),
    )

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
            directErrors = emptySet(),
        )
    }

    override val computedReachableDeclarations: CyclicComputation<ReachableDeclarationSet> by lazy {
        image.computedReachableDeclarations.transform {
            it.copy(
                reachableDeclarations = it.reachableDeclarations - argumentDeclarationBlock.argumentDeclarations,
            )
        }
    }

    override val subExpressions: Set<Expression> by lazy { setOfNotNull(image) }
}
