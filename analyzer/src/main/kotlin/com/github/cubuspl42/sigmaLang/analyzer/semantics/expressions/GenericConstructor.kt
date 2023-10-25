package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TableValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.lazier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.chainWithIfNotNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
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
            val outerMetaScope = context.outerMetaScope

            val metaArgumentDeclarationBuildOutput = AbstractionConstructor.ArgumentDeclaration.build(
                outerMetaScope = outerMetaScope,
                argumentTypeTerm = term.metaArgumentType,
            )

            val metaArgumentDeclaration by metaArgumentDeclarationBuildOutput.argumentDeclarationLazy
            val metaArgumentDeclarationBlock by metaArgumentDeclarationBuildOutput.argumentDeclarationBlockLazy

            val bodyLazy = lazier {
                val innerMetaScope = metaArgumentDeclarationBlock.chainWithIfNotNull(
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
                inferredType = object : GenericType(
                    metaArgumentType = metaArgumentType,
                ) {
                    override fun specify(metaArgument: DictValue): Type {
                        val typeVariableReplacer = buildTypeVariableReplacer(
                            traitType = metaArgumentType,
                            path = TypeVariable.Path.Root,
                            specificationTable = metaArgument,
                            traitDeclaration = metaArgumentDeclaration,
                        )

                        val specifiedType = inferredBodyType.replaceType(
                            typeReplacer = typeVariableReplacer,
                        ) as Type

                        return specifiedType
                    }
                },
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

private fun buildTypeVariableReplacer(
    traitDeclaration: Declaration,
    path: TypeVariable.Path,
    traitType: TupleType,
    specificationTable: TableValue,
): TypeAlike.TypeReplacer = TypeAlike.TypeReplacer.combineAll(
    replacers = traitType.entries.map { entry ->
        val entryKey = entry.key
        val specificationValue = specificationTable.read(entryKey)!!.value!!

        buildTypeVariableReplacer(
            traitDeclaration = traitDeclaration,
            entryPath = path.extend(entryKey),
            traitEntryType = entry.type,
            specificationValue = specificationValue,
        )
    },
)

private fun buildTypeVariableReplacer(
    traitDeclaration: Declaration,
    entryPath: TypeVariable.Path,
    traitEntryType: TypeAlike,
    specificationValue: Value,
): TypeAlike.TypeReplacer = when (traitEntryType) {
    TypeType -> {
        val specificationType = specificationValue.asType!!

        object : TypeAlike.TypeReplacer {
            override fun replace(
                type: TypeAlike,
            ): TypeAlike? =
                if (type is TypeVariable && type.traitDeclaration == traitDeclaration && type.path == entryPath) {
                    specificationType
                } else {
                    null
                }
        }
    }

    is TupleType -> {
        val innerSpecificationTable = specificationValue as TableValue

        buildTypeVariableReplacer(
            traitDeclaration = traitDeclaration,
            path = entryPath,
            traitType = traitEntryType,
            specificationTable = innerSpecificationTable,
        )
    }

    else -> throw UnsupportedOperationException("Invalid trait")
}
