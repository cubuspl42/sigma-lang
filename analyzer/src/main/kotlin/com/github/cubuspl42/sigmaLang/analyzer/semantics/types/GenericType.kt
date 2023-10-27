package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TableValue

class GenericType(
    override val parameterType: TupleType,
    val typeAbstraction: TypeAbstraction,
) : ParametrizedType() {
    interface TypeAbstraction {
        fun apply(parameterTable: TableValue): Type
    }

    override fun parametrize(
        metaArgument: DictValue,
    ): Type {
        return typeAbstraction.apply(parameterTable = metaArgument)
    }
}
