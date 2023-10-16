package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
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
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AbstractionConstructor : Expression() {
    abstract val metaArgumentType: TupleType?

    abstract val argumentType: TupleType

    // TODO: This is a terrible hack
    open val argumentDeclarationBlock by lazy { argumentType.toArgumentDeclarationBlock() }

//    abstract val argumentDeclarationBlock: ArgumentStaticBlock

    abstract val declaredImageType: TypeAlike?

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

            return object : Stub<AbstractionConstructor> {
                override val resolved: AbstractionConstructor by lazy {
                    val metaArgumentTypeConstructorStub = term.metaArgumentType?.let {
                        TypeExpression.build(
                            outerMetaScope = outerMetaScope,
                            term = it,
                        )
                    }

                    val metaArgumentTypeConstructor = metaArgumentTypeConstructorStub?.resolved

                    val metaArgumentTypeConstructorAnalysis = metaArgumentTypeConstructor?.evaluateAsType()

                    val metaArgumentType = metaArgumentTypeConstructorAnalysis?.typeOrIllType?.let { it as TupleType }

                    val typeVariableBlock = metaArgumentType?.buildTypeVariableBlock()

                    val typePlaceholderBlock = typeVariableBlock?.let {
                        TupleType.TypePlaceholderBlock(
                            typeVariableBlock = it,
                        )
                    }

                    val innerTypePlaceholderMetaScope = typePlaceholderBlock.chainWithIfNotNull(
                        outerScope = outerMetaScope,
                    )

                    val argumentTypeConstructor = term.argumentType.let {
                        TypeExpression.build(
                            outerMetaScope = innerTypePlaceholderMetaScope,
                            term = it,
                        )
                    }.resolved

                    val argumentType = argumentTypeConstructor.evaluateAsType().typeOrIllType as TupleType

                    val innerTypeVariableMetaScope = typeVariableBlock.chainWithIfNotNull(
                        outerScope = outerMetaScope,
                    )

                    val internalArgumentTypeConstructor = term.argumentType.let {
                        TypeExpression.build(
                            outerMetaScope = innerTypeVariableMetaScope,
                            term = it,
                        )
                    }.resolved

                    val internalArgumentType = internalArgumentTypeConstructor.evaluateAsType().typeOrIllType as TupleType

                    val argumentDeclarationBlock = internalArgumentType.toArgumentDeclarationBlock()

                    val innerScope = argumentDeclarationBlock.chainWith(
                        outerScope = outerScope,
                    )

                    val declaredImageTypeBody = term.declaredImageType?.let {
                        TypeExpression.build(
                            outerMetaScope = innerTypePlaceholderMetaScope,
                            term = it,
                        ).resolved
                    }

                    val declaredImageTypeAnalysis = declaredImageTypeBody?.evaluateAsType()

                    val image = build(
                        context = BuildContext(
                            outerMetaScope = innerTypeVariableMetaScope,
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

                        override val declaredImageType: TypeAlike? = declaredImageTypeAnalysis?.typeOrIllType

                        override val image by lazy { image.resolved }
                    }
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

                override val inferredType: TypeAlike = run {
                    val effectiveImageType = this@AbstractionConstructor.declaredImageType ?: run {
                        imageAnalysis?.inferredType ?: IllType
                    }

                    UniversalFunctionType(
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
