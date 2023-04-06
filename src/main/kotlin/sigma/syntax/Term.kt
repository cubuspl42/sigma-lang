package sigma.syntax

import sigma.StaticTypeScope
import sigma.StaticValueScope

abstract class Term {
    abstract val location: SourceLocation

    // TODO: Improve this! Merge with `inferType`?
    open fun validate(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
    }
}
