package sigma.values

import sigma.StaticScope
import sigma.StaticValueScope
import sigma.expressions.Declaration
import sigma.types.Type

interface TypeThunk {
    fun obtain(): Type
}

data class FixedStaticValueScope(
    private val entries: Map<Symbol, Type>
) : StaticValueScope {
    override fun getValueType(
        valueName: Symbol,
    ): Type? = entries[valueName]
}

class LoopedStaticValueScope(
    private val context: StaticScope,
    declarations: Iterable<Declaration>,
) : StaticValueScope {
    private val declarationByName = declarations.associateBy { it.name }

    private val scope: StaticScope = context.copy(
        valueScope = this,
    )

    fun validate() {
        declarationByName.values.forEach {
            val inferredType = it.inferType(scope = scope)
            val declaredType = it.determineDeclaredType(scope = scope) ?: return@forEach

            if (!inferredType.isAssignableTo(declaredType)) {
                throw TypeError(
                    message = "Value ${it.name.dump()} has declared type ${declaredType.dump()}, but its inferred type is ${inferredType.dump()}",
                )
            }
        }
    }

    override fun getValueType(
        valueName: Symbol,
    ): Type? = declarationByName[valueName]?.determineAssumedType(
        scope = scope,
    ) ?: context.getValueType(
        valueName = valueName,
    )
}
