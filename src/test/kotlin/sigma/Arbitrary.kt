package sigma

import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.OrderedTupleType
import sigma.types.UnorderedTupleType
import sigma.values.Symbol

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
