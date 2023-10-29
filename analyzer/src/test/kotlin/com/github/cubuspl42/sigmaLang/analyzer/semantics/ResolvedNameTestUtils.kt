package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.CallMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FieldReadMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.IntLiteralMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ReferenceMatcher
import utils.Matcher
import utils.checked

fun ResolvedIntroduction.buildReferenceMatcher(): Matcher<Expression> = when (this) {
    is ResolvedDefinition -> Matcher.Equals(this.body)
    is ResolvedAbstractionArgument -> this.buildArgumentReferenceMatcher()
}

fun ResolvedAbstractionArgument.buildArgumentReferenceMatcher(): Matcher<Expression> {
    val subjectMatcher: Matcher<Expression> = ReferenceMatcher(
        referredDeclaration = Matcher.Equals(this.argumentDeclaration),
    ).checked()

    return when (this) {
        is ResolvedOrderedArgument -> CallMatcher(
            subject = subjectMatcher,
            argument = IntLiteralMatcher(
                value = Matcher.Equals(this.index),
            ).checked(),
        ).checked()

        is ResolvedUnorderedArgument -> FieldReadMatcher(
            subject = subjectMatcher,
            fieldName = Matcher.Equals(this.name),
        ).checked()
    }
}
