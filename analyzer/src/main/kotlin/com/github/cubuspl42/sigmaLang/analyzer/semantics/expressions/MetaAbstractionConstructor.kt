package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm

fun <T> lazier(
    block: () -> Lazy<T>,
): Lazy<T> = object : Lazy<T> {
    val valueLazy = lazy { block() }
    override val value: T
        get() = valueLazy.value.value

    override fun isInitialized(): Boolean = valueLazy.isInitialized() && valueLazy.value.isInitialized()
}

class MetaAbstractionConstructor(
    override val term: AbstractionConstructorTerm?,
    private val metaArgumentTypeLazy: Lazy<TupleType>,
    private val bodyLazy: Lazy<Expression>,
) : Expression() {
    companion object {
        fun build(
            context: Expression.BuildContext,
            metaArgumentTypeTerm: TupleTypeConstructorTerm,
            term: AbstractionConstructorTerm,
        ): Lazy<MetaAbstractionConstructor> {
            val outerMetaScope = context.outerMetaScope

            val metaArgumentTypeConstructor by TupleTypeConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = BuiltinScope,
                    outerScope = outerMetaScope,
                ),
                term = metaArgumentTypeTerm,
            ).asLazy()

            val metaArgumentTypeThunk by lazy {
                metaArgumentTypeConstructor.constClassified!!.valueThunk.thenJust { it.asType as TupleType }
            }

            val bodyLazy = lazier {
                val metaArgumentType = metaArgumentTypeThunk.value!! // TODO: Depend on tuple constructor term instead?

                val typeVariableBlock = metaArgumentType.buildTypeVariableBlock()

                val innerMetaScope = typeVariableBlock.chainWithIfNotNull(
                    outerScope = outerMetaScope,
                )

                AbstractionConstructor.buildDirectly(
                    context = context.copy(
                        outerMetaScope = innerMetaScope,
                    ),
                    term = term,
                ).asLazy()
            }

            return lazy {
                MetaAbstractionConstructor(
                    term = term,
                    metaArgumentTypeLazy = metaArgumentTypeThunk.asLazy(),
                    bodyLazy = bodyLazy,
                )
            }
        }
    }

    val metaArgumentType by metaArgumentTypeLazy

    val body by bodyLazy

    override val outerScope: StaticScope
        get() = StaticScope.Empty

    // TODO: A util to get the inferred type if the expression is first-order OR all meta arguments are inferrable
    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = Expression.Computation.pure(
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = GenericType(
                    metaArgumentType = metaArgumentType,
                ),
            ),
            directErrors = emptySet(), // TODO
        )
    )

    override val subExpressions: Set<Expression>
        get() = setOf(body)

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> =
        body.bindDirectly(dynamicScope = dynamicScope)
}
