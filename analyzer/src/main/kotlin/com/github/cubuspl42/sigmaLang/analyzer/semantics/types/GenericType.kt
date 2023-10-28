package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TableValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

class GenericType(
    private val parameterDeclaration: ArgumentDeclaration,
    /**
     * Body type with [TypeVariable]s referring to the parameter declaration
     */
    private val bodyType: Type,
) : ParametrizedType() {
    companion object {
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
    }

    override val parameterType: TupleType
        get() = parameterDeclaration.declaredType

    override fun parametrize(
        metaArgument: DictValue,
    ): Type {
        val typeVariableReplacer = buildTypeVariableReplacer(
            traitType = parameterType,
            path = TypeVariable.Path.Root,
            specificationTable = metaArgument,
            traitDeclaration = parameterDeclaration,
        )

        val specifiedType = bodyType.replaceType(
            typeReplacer = typeVariableReplacer,
        ) as Type

        return specifiedType
    }

    override fun dumpDirectly(depth: Int): String = "${parameterType.dumpRecursively(depth)} !-> ${bodyType.dumpRecursively(depth = depth)}"
}
