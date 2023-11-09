package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import utils.Matcher
import utils.checked

class ReferenceMatcher(
    private val referredDeclaration: Matcher<Declaration>,
) : Matcher<Reference>() {
    companion object {
        fun referring(
            declaration: Declaration,
        ): Matcher<Reference> = ReferenceMatcher(
            referredDeclaration = Matcher.Equals(declaration),
        )

        fun orderedArgument(
            declaration: Declaration,
            argumentName: Identifier,
        ): Matcher<Expression> {
            val argumentTupleType = declaration.declaredType as? OrderedTupleType ?: throw IllegalArgumentException()
            val argumentIndex = argumentTupleType.getIndexByName(name = argumentName) ?: throw IllegalArgumentException()

            return CallMatcher.arrayIndex(
                array = referring(declaration).checked(),
                index = argumentIndex.toLong(),
            ).checked()
        }
    }

    override fun match(actual: Reference) {
        referredDeclaration.match(actual = actual.referredDeclaration)
    }
}
