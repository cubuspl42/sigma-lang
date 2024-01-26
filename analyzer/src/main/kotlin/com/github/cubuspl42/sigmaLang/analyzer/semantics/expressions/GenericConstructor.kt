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
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm

class GenericConstructor(
    override val term: GenericConstructorTerm?,
    private val metaArgumentDeclarationLazy: Lazy<ArgumentDeclaration>,
    private val bodyLazy: Lazy<Expression>,
) : Expression() {
    companion object {
        fun build(
            context: Expression.BuildContext,
            metaArgumentTerm: TupleTypeConstructorTerm,
            buildBody: (innerScope: StaticScope) -> Lazy<Expression>,
        ): Lazy<GenericConstructor> {
            val metaArgumentDeclarationBuildOutput = AbstractionConstructor.ArgumentDeclaration.build(
                outerScope = context.outerScope,
                argumentTypeTerm = metaArgumentTerm,
            )

            val metaArgumentDeclarationBlock by metaArgumentDeclarationBuildOutput.argumentDeclarationBlockLazy

            val bodyLazy = lazier {
                val innerScope = metaArgumentDeclarationBlock.chainWithIfNotNull(
                    outerScope = context.outerScope,
                )

                buildBody(innerScope)
            }

            return lazy {
                GenericConstructor(
                    term = null,
                    metaArgumentDeclarationLazy = metaArgumentDeclarationBuildOutput.argumentDeclarationLazy,
                    bodyLazy = bodyLazy,
                )
            }
        }

        fun build(
            context: Expression.BuildContext,
            term: GenericConstructorTerm,
        ): Lazy<GenericConstructor> = build(
            context = context,
            metaArgumentTerm = term.metaArgumentType,
            buildBody = { innerScope ->
                Expression.build(
                    context = BuildContext(
                        outerScope = innerScope,
                    ),
                    term = term.body,
                ).asLazy()
            },
        )
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
            typeInference = TypeInference(
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

