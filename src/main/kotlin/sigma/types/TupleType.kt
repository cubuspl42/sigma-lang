package sigma.types

import sigma.StaticValueScope
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.tables.Scope
import sigma.values.tables.Table

abstract class TupleType : TableType() {
    abstract fun toStaticValueScope(): StaticValueScope
}
