package sigma.syntax.expressions

import sigma.evaluation.values.Symbol

interface OrderedTupleTypeConstructorTerm {
    interface Element {
        val name: Symbol?

        val type: ExpressionTerm
    }

    val elements: List<Element>
}
