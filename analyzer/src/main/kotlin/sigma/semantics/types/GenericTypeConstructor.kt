package sigma.semantics.types

import sigma.semantics.StaticScope
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.GenericParametersTuple

class GenericTypeConstructor(
    val context: StaticScope,
    val argumentMetaType: GenericParametersTuple,
    val bodyTerm: ExpressionTerm,
    val body: TypeEntity,
) : TypeEntity() {
    fun call(passedArgument: OrderedTypeTuple): TypeEntity {
        // TODO
//        return bodyTerm.evaluate(declarationScope = context)
        TODO()
    }
}
