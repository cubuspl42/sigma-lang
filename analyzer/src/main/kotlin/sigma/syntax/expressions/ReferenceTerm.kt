package sigma.syntax.expressions

import sigma.evaluation.values.Symbol

interface ReferenceTerm {
    val referredName: Symbol
}