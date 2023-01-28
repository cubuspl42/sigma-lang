package sigma.compiler

import sigma.syntax.expressions.Expression
import sigma.syntax.expressions.LetExpression

class Compiler(
    private val prelude: Prelude,
) {
    companion object {
        fun initialize(): Compiler {
            val prelude = Prelude.load()

            return Compiler(
                prelude = prelude,
            )
        }
    }

    fun load(
        sourceName: String,
        source: String,
    ): Program = Program(
        prelude = prelude,
        root = parse(
            sourceName = sourceName,
            source = source,
        ),
    )

    private fun parse(
        sourceName: String,
        source: String,
    ): Expression = LetExpression.build(
        ctx = Program.buildParser(
            sourceName = sourceName,
            source = source,
        ).program().letExpression(),
    )
}
