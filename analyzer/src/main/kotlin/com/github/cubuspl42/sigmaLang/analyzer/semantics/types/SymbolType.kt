package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class SymbolType(
    override val value: Symbol,
) : PrimitiveType(), PrimitiveLiteralType {
    companion object {
        fun of(
            name: String,
        ): SymbolType = SymbolType(
            value = Identifier.of(name = name),
        )
    }

    override fun dumpDirectly(depth: Int): String = value.dump()

    override val asLiteral = this

    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = when (other) {
        this -> this
        else -> AnyType
    }

    override val asPrimitiveType: PrimitiveType = this
}
