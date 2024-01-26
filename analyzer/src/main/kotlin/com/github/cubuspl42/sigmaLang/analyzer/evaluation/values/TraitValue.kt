package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue

class TraitValue(
    private val traitDeclaration: Declaration,
    private val traitType: TupleType,
    private val path: TypeVariable.Path,
) : TableValue() {
    override fun read(key: PrimitiveValue): Thunk<Value>? {
        val innerPath = path.extend(key)

        return traitType.getTypeByKey(key = key)?.let { innerType ->
            Thunk.pure(
                when (innerType) {
                    TypeType -> TypeVariable(
                        traitDeclaration = traitDeclaration,
                        path = innerPath,
                    ).asValue

                    is TupleType -> TraitValue(
                        traitDeclaration = traitDeclaration,
                        traitType = innerType,
                        path = innerPath,
                    )

                    else -> throw UnsupportedOperationException("Invalid trait")
                },
            )
        }

    }

    override fun dump(): String = "(trait value)"
}
