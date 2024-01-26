package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.asLazy
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike

interface AbstractionConstructorTerm : ExpressionTerm {
    class ArgumentDeclaration(
        override val declaredType: TupleType,
    ) : Declaration {
        data class Analysis(
            val argumentDeclarationLazy: Lazy<ArgumentDeclaration>,
            val argumentDeclarationBlockLazy: Lazy<StaticBlock>,
        )

        companion object {
            fun analyze(
                outerScope: StaticScope,
                argumentTypeTerm: TupleTypeConstructorTerm,
            ): Analysis {
                val argumentDeclarationLazy = lazy {
                    val argumentTypeConstructor = argumentTypeTerm.let {
                        TypeExpression.build(
                            outerScope = outerScope,
                            term = it,
                        )
                    }.resolved

                    val argumentType = argumentTypeConstructor.evaluateAsType().typeOrIllType as TupleType

                    ArgumentDeclaration(
                        declaredType = argumentType,
                    )
                }

                val argumentDeclarationBlockLazy = lazy {
                    argumentTypeTerm.toArgumentDeclarationBlock(
                        argumentDeclaration = argumentDeclarationLazy.value,
                    )
                }

                return Analysis(
                    argumentDeclarationLazy = argumentDeclarationLazy,
                    argumentDeclarationBlockLazy = argumentDeclarationBlockLazy,
                )
            }
        }
    }

    data class Analysis(
        val expressionLazy: Lazy<AbstractionConstructor>,
        val argumentDeclarationBlockLazy: Lazy<StaticBlock>,
    ) {
        val expression: AbstractionConstructor by expressionLazy
        val argumentDeclarationBlock: StaticBlock by argumentDeclarationBlockLazy
    }

    companion object {
        fun analyze(
            context: Expression.BuildContext,
            term: AbstractionConstructorTerm,
        ): AbstractionConstructorTerm.Analysis {
            val outerScope = context.outerScope

            val argumentDeclarationBuildOutput = ArgumentDeclaration.analyze(
                outerScope = outerScope,
                argumentTypeTerm = term.argumentType,
            )

            val argumentDeclaration by argumentDeclarationBuildOutput.argumentDeclarationLazy
            val argumentDeclarationBlock by argumentDeclarationBuildOutput.argumentDeclarationBlockLazy

            val expressionStub = object : Stub<AbstractionConstructor> {
                override val resolved: AbstractionConstructor by lazy {
                    val declaredImageTypeBody = term.declaredImageType?.let {
                        TypeExpression.build(
                            outerScope = context.outerScope,
                            term = it,
                        ).resolved
                    }

                    val declaredImageTypeAnalysis = declaredImageTypeBody?.evaluateAsType()

                    val innerScope = argumentDeclarationBlock.chainWith(
                        outerScope = outerScope,
                    )

                    val image = Expression.build(
                        context = Expression.BuildContext(
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

            return AbstractionConstructorTerm.Analysis(
                expressionLazy = expressionStub.asLazy(),
                argumentDeclarationBlockLazy = argumentDeclarationBuildOutput.argumentDeclarationBlockLazy,
            )
        }
    }

    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionTerm?

    val image: ExpressionTerm

}
