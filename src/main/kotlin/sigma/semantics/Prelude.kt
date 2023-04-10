package sigma.semantics

import getResourceAsText
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.SyntaxValueScope
import sigma.syntax.expressions.LocalScopeTerm
import sigma.values.tables.Scope

data class Prelude(
    val valueScope: SyntaxValueScope,
    val scope: Scope,
) {
    companion object {
        fun load(): Prelude {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            val prelude = LocalScopeTerm.parse(
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
