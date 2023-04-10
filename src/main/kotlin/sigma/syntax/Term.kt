package sigma.syntax

import sigma.SyntaxTypeScope
import sigma.SyntaxValueScope

abstract class Term {
    abstract val location: SourceLocation

    // TODO: Improve this! Merge with `inferType`?
    open fun validate(
        typeScope: SyntaxTypeScope,
        valueScope: SyntaxValueScope,
    ) {
    }
}
