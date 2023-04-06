package sigma.values

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.syntax.Declaration
import sigma.semantics.types.Type

data class FixedStaticValueScope(
    private val entries: Map<Symbol, Type>,
) : StaticValueScope {
    override fun getValueType(
        valueName: Symbol,
    ): Type? = entries[valueName]
}

class LoopedStaticValueScope(
    private val typeContext: StaticTypeScope,
    private val valueContext: StaticValueScope,
    declarations: Iterable<Declaration>,
) : StaticValueScope {
    private val declarationByName = declarations.associateBy { it.name }

    // TODO: Validate this stuff again?
//    fun validate() {
//        declarationByName.values.forEach {
//            val inferredType = it.inferType(
//                typeScope = typeContext,
//                valueScope = this,
//            )
//
//            val declaredType = it.determineDeclaredType(
//                typeScope = typeContext,
//            ) ?: return@forEach
//
//            if (!inferredType.isAssignableTo(declaredType)) {
//                throw TypeError(
//                    message = "Value ${it.name.dump()} has declared type ${declaredType.dump()}, but its inferred type is ${inferredType.dump()}",
//                )
//            }
//        }
//    }

    override fun getValueType(
        valueName: Symbol,
    ): Type? = declarationByName[valueName]?.determineAssumedType(
        typeScope = typeContext,
        valueScope = this,
    ) ?: valueContext.getValueType(
        valueName = valueName,
    )
}
