package sigma.semantics.types

import sigma.semantics.StaticScope
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.GenericParametersTuple

class GenericTypeConstructor(
    val context: StaticScope,
    val argumentMetaType: GenericParametersTuple,
    val bodyTerm: ExpressionSourceTerm,
    val body: TypeEntity,
) : TypeEntity() {
    fun call(passedArgument: OrderedTypeTuple): TypeEntity {
        // TODO
//        return bodyTerm.evaluate(declarationScope = context)
        TODO()
    }
}
