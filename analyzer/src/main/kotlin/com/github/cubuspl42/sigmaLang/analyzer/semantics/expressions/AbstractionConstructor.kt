package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableAbstraction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AbstractionConstructor : FirstOrderExpression() {
    abstract val argumentDeclaration: AbstractionConstructorTerm.ArgumentDeclaration

    val argumentType: TupleType
        get() = argumentDeclaration.declaredType

//    abstract val argumentDeclarationBlock: ArgumentStaticBlock

    abstract val declaredImageType: TypeAlike?

    abstract val image: Expression

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(
        ComputableAbstraction(
            outerDynamicScope = dynamicScope,
            argumentDeclaration = argumentDeclaration,
            image = image,
        ),
    )

    override val computedAnalysis = buildAnalysisComputation {
        Analysis(
            typeInference = object : TypeInference() {
                private val imageAnalysis by lazy {
                    compute(image.computedTypeInference)
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

fun AbstractionConstructor(
    // TODO: Remove this argument, loop instead
    argumentDeclaration: AbstractionConstructorTerm.ArgumentDeclaration,
    declaredImageTypeLazy: Lazy<TypeAlike?>,
    imageLazy: Lazy<Expression>,
): AbstractionConstructor = object : AbstractionConstructor() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ExpressionTerm? = null

    override val argumentDeclaration: AbstractionConstructorTerm.ArgumentDeclaration = argumentDeclaration

    override val declaredImageType: TypeAlike? by declaredImageTypeLazy

    override val image: Expression by imageLazy
}
