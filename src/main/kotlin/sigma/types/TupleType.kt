package sigma.types

import sigma.values.PrimitiveValue
import sigma.values.Symbol

abstract class TupleType : TableType() {
    abstract val valueTypeByKey: Map<PrimitiveValue, Type>

    abstract val valueTypeByLabel: Map<Symbol, Type>
}
