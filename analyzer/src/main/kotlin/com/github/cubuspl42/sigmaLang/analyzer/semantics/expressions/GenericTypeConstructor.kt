package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class GenericTypeConstructor(
    val metaArgumentType: TupleType,
    val typeImage: Expression,
) : TypeConstructor() {
    override val outerScope: StaticScope
        get() = StaticScope.Empty
    override val term: ExpressionTerm?
        get() = null
    override val subExpressions: Set<Expression>
        get() = setOf(typeImage)

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> {
        TODO()

//        return Thunk.pure(
//            GenericType(
//                metaArgumentType = metaArgumentType,
//            ).asValue
//        )
    }
}
