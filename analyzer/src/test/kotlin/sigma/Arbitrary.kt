package sigma

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.Symbol

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
