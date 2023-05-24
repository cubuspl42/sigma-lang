package sigma

import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.TupleType

object Arbitrary {
    val unorderedTupleType = TupleType.unordered(
        TupleType.UnorderedEntry(
            name = Symbol.of("foo"),
            type = BoolType,
        ),
        TupleType.UnorderedEntry(
            name = Symbol.of("bar"),
            type = IntCollectiveType,
        ),
    )

    val orderedTupleType = TupleType.ordered(
        TupleType.OrderedEntry(
            index = 0,
            name = null,
            type = BoolType,
        ),
        TupleType.OrderedEntry(
            index = 1,
            name = null,
            type = IntCollectiveType,
        ),
    )
}
