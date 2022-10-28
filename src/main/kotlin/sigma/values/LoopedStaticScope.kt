package sigma.values

import sigma.StaticScope
import sigma.StaticValueScope
import sigma.TypeExpression
import sigma.expressions.Declaration
import sigma.types.Type

class LoopedStaticValueScope(
    private val context: StaticScope,
    private val declarations: Map<Symbol, Declaration>,
) : StaticValueScope {
    private val scope: StaticScope = context.copy(
        valueScope = this,
    )

    fun validate() {
        declarations.values.forEach {
            val inferredType = it.inferType(scope = scope)
            val declaredType = it.determineDeclaredType(scope = scope) ?: return@forEach

            if (declaredType != inferredType) {
                throw TypeError(
                    message = "Value ${it.name.dump()} has declared type ${declaredType.dump()}, but its inferred type is ${inferredType.dump()}",
                )
            }
        }
    }

    override fun getValueType(
        valueName: Symbol,
    ): Type? = declarations[valueName]?.determineAssumedType(
        scope = scope,
    ) ?: context.getValueType(
        valueName = valueName,
    )
}
