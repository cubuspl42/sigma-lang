package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser

interface UnorderedTupleConstructorTerm {
    val entries: List<Entry>

    interface Entry {
        val name: Symbol

        val value: ExpressionTerm
    }
}
