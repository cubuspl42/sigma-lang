package com.github.cubuspl42.sigmaLang.analyzer

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

object Arbitrary {
    val unorderedTupleType = UnorderedTupleType(
        valueTypeByName = mapOf(
            Symbol.of("foo") to BoolType,
            Symbol.of("bar") to IntCollectiveType,
        ),
    )

    val orderedTupleType = OrderedTupleType(
        elements = listOf(
            OrderedTupleType.Element(
                name = null,
                type = BoolType,
            ),
            OrderedTupleType.Element(
                name = null,
                type = IntCollectiveType,
            ),
        ),
    )
}
