package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.lazier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericConstructorTerm

class GenericConstructor(
    override val term: GenericConstructorTerm,
    private val metaArgumentTypeLazy: Lazy<TupleType>,
    private val bodyLazy: Lazy<Expression>,
) : Expression() {
    companion object {
        fun build(
            context: Expression.BuildContext,
            term: GenericConstructorTerm,
        ): Lazy<GenericConstructor> {
            val outerMetaScope = context.outerMetaScope

            val metaArgumentTypeConstructor by TupleTypeConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = BuiltinScope,
                    outerScope = outerMetaScope,
                ),
                term = term.metaArgumentType,
            ).asLazy()

            val metaArgumentTypeThunk by lazy {
                metaArgumentTypeConstructor.constClassified!!.valueThunk.thenJust { it.asType as TupleType }
            }

            val bodyLazy = lazier {
                val metaArgumentType = metaArgumentTypeThunk.value!!

                val typeVariableBlock = metaArgumentType.buildTypeVariableBlock()

                val innerMetaScope = typeVariableBlock.chainWithIfNotNull(
                    outerScope = outerMetaScope,
                )

                Expression.build(
                    context = context.copy(
                        outerMetaScope = innerMetaScope,
                    ),
                    term = term.body,
                ).asLazy()
            }

            return lazy {
                GenericConstructor(
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
                inferredType = object : GenericType(
                    metaArgumentType = metaArgumentType,
                ) {
                    override fun specify(metaArgument: DictValue): Type {
                        TODO("Not yet implemented")
                    }
                },
            ),
            directErrors = emptySet(), // TODO
        )
    )

    override val subExpressions: Set<Expression>
        get() = setOf(body)

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = body.bindDirectly(dynamicScope = dynamicScope)
}
