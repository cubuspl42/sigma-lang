package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AbstractionConstructor : FirstOrderExpression() {
    abstract val argumentDeclaration: ArgumentDeclaration

    val argumentType: TupleType
        get() = argumentDeclaration.declaredType

//    abstract val argumentDeclarationBlock: ArgumentStaticBlock

    abstract val declaredImageType: TypeAlike?

    abstract val image: Expression

    class ArgumentDeclaration(
        override val declaredType: TupleType,
    ) : Declaration

    data class BuildOutput(
        val expressionLazy: Lazy<AbstractionConstructor>,
        val argumentDeclarationBlockLazy: Lazy<StaticBlock>,
    ) {
        val expression: AbstractionConstructor by expressionLazy
        val argumentDeclarationBlock: StaticBlock by argumentDeclarationBlockLazy
    }

    companion object {
        fun build(
            context: BuildContext,
            term: AbstractionConstructorTerm,
        ): BuildOutput {
            val outerMetaScope = context.outerMetaScope
            val outerScope = context.outerScope

            val argumentDeclaration by lazy {
                val argumentTypeConstructor = term.argumentType.let {
                    TypeExpression.build(
                        outerMetaScope = outerMetaScope,
                        term = it,
                    )
                }.resolved

                val argumentType = argumentTypeConstructor.evaluateAsType().typeOrIllType as TupleType

                ArgumentDeclaration(
                    declaredType = argumentType,
                )
            }

            val argumentDeclarationBlockLazy = lazy {
                term.argumentType.toArgumentDeclarationBlock(
                    argumentDeclaration = argumentDeclaration,
                )
            }

            val argumentDeclarationBlock by argumentDeclarationBlockLazy

            val expressionStub = object : Stub<AbstractionConstructor> {
                override val resolved: AbstractionConstructor by lazy {
                    val declaredImageTypeBody = term.declaredImageType?.let {
                        TypeExpression.build(
                            outerMetaScope = outerMetaScope,
                            term = it,
                        ).resolved
                    }

                    val declaredImageTypeAnalysis = declaredImageTypeBody?.evaluateAsType()

                    val innerScope = argumentDeclarationBlock.chainWith(
                        outerScope = outerScope,
                    )

                    val image = build(
                        context = BuildContext(
                            outerMetaScope = outerMetaScope,
                            outerScope = innerScope,
                        ),
                        term = term.image,
                    )

                    return@lazy object : AbstractionConstructor() {
                        override val outerScope: StaticScope = context.outerScope

                        override val term: ExpressionTerm = term

                        override val argumentDeclaration = argumentDeclaration

                        override val declaredImageType: TypeAlike? = declaredImageTypeAnalysis?.typeOrIllType

                        override val image by lazy { image.resolved }
                    }
                }
            }

            return BuildOutput(
                expressionLazy = expressionStub.asLazy(),
                argumentDeclarationBlockLazy = argumentDeclarationBlockLazy,
            )
        }
    }

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(
        ComputableAbstraction(
            outerDynamicScope = dynamicScope,
            argumentDeclaration = argumentDeclaration,
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
                reachableDeclarations = it.reachableDeclarations - argumentDeclaration,
            )
        }
    }

    override val subExpressions: Set<Expression> by lazy { setOfNotNull(image) }
}
