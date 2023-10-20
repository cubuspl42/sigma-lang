package com.github.cubuspl42.sigmaLang.analyzer

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

object Arbitrary {
    val unorderedTupleType = UnorderedTupleType(
        valueTypeByName = mapOf(
            Identifier.of("foo") to BoolType,
            Identifier.of("bar") to IntCollectiveType,
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
