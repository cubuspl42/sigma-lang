package sigma.compiler

import getResourceAsText
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.StaticValueScope
import sigma.expressions.LocalScope
import sigma.values.tables.Scope

data class Prelude(
    val valueScope: StaticValueScope,
    val scope: Scope,
) {
    companion object {
        fun load(): Prelude {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            val prelude = LocalScope.parse(
                sourceName = "prelude",
                source = preludeSource,
            )

            val preludeValueScope = prelude.evaluateStatically(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            val preludeScope = prelude.evaluateDynamically(
                scope = BuiltinScope,
            )

            return Prelude(
                valueScope = preludeValueScope,
                scope = preludeScope,
            )
        }
    }
}
