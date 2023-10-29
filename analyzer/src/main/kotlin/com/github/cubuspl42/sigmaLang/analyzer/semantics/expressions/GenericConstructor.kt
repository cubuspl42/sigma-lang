package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.lazier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericConstructorTerm

class GenericConstructor(
    override val term: GenericConstructorTerm,
    private val metaArgumentDeclarationLazy: Lazy<ArgumentDeclaration>,
    private val bodyLazy: Lazy<Expression>,
) : Expression() {
    companion object {
        fun build(
            context: Expression.BuildContext,
            term: GenericConstructorTerm,
        ): Lazy<GenericConstructor> {
            val metaArgumentDeclarationBuildOutput = AbstractionConstructor.ArgumentDeclaration.build(
                outerScope = context.outerScope,
                argumentTypeTerm = term.metaArgumentType,
            )

            val metaArgumentDeclaration by metaArgumentDeclarationBuildOutput.argumentDeclarationLazy
            val metaArgumentDeclarationBlock by metaArgumentDeclarationBuildOutput.argumentDeclarationBlockLazy

            val bodyLazy = lazier {
                val innerScope = metaArgumentDeclarationBlock.chainWithIfNotNull(
                    outerScope = context.outerScope,
                )

                Expression.build(
                    context = BuildContext(
                        outerScope = innerScope,
                    ),
                    term = term.body,
                ).asLazy()
            }

            return lazy {
                GenericConstructor(
                    term = term,
                    metaArgumentDeclarationLazy = metaArgumentDeclarationBuildOutput.argumentDeclarationLazy,
                    bodyLazy = bodyLazy,
                )
            }
        }
    }

    val metaArgumentDeclaration by metaArgumentDeclarationLazy

    val metaArgumentType: TupleType
        get() = metaArgumentDeclaration.declaredType

    val body by bodyLazy

    override val outerScope: StaticScope
        get() = StaticScope.Empty

    // TODO: A util to get the inferred type if the expression is first-order OR all meta arguments are inferrable
    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = buildDiagnosedAnalysisComputation {
        val inferredBodyType = compute(body.inferredTypeOrIllType) as Type

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = GenericType(
                    parameterDeclaration = metaArgumentDeclaration,
                    bodyType = inferredBodyType,
                )
            ),
            directErrors = emptySet(), // TODO
        )
    }

    override val subExpressions: Set<Expression>
        get() = setOf(body)

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = body.bindDirectly(dynamicScope = dynamicScope)
}

