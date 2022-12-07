package sigma

import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.OrderedTupleType
import sigma.types.UnorderedTupleType
import sigma.values.Symbol

object Arbitrary {
    val unorderedTupleType = UnorderedTupleType(
        valueTypeByKey = mapOf(
            Symbol.of("foo") to BoolType,
            Symbol.of("bar") to IntCollectiveType,
        ),
    )

    val orderedTupleType = OrderedTupleType(
        entries = listOf(
            OrderedTupleType.Entry(
                name = null,
                elementType = BoolType,
            ),
            OrderedTupleType.Entry(
                name = null,
                elementType = IntCollectiveType,
            ),
        ),
    )
}
