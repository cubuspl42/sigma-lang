package sigma.syntax.expressions

import sigma.syntax.SourceLocation

abstract class Term {
    abstract val location: SourceLocation
}
