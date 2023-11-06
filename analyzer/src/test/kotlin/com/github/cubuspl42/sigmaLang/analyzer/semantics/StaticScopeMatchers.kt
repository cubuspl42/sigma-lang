package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import utils.Matcher
import utils.checked

object StaticScopeMatchers {
    class LevelResolvedIntroductionMatcher(
        private val level: Matcher<StaticScope.Level>,
        private val resolvedIntroduction: Matcher<ResolvedIntroduction>,
    ) : Matcher<LeveledResolvedIntroduction>() {
        companion object {
            fun primaryIntroductionMatcher(
                resolvedIntroduction: Matcher<ResolvedIntroduction>,
            ): LevelResolvedIntroductionMatcher = LevelResolvedIntroductionMatcher(
                level = Matcher.Equals(StaticScope.Level.Primary),
                resolvedIntroduction = resolvedIntroduction,
            )


            fun primaryArgumentDeclarationMatcher(
                argumentDeclaration: AbstractionConstructor.ArgumentDeclaration,
            ): LevelResolvedIntroductionMatcher = primaryIntroductionMatcher(
                resolvedIntroduction = ResolvedIntroductionMatchers.ResolvedAbstractionArgumentMatcher(
                    argumentDeclaration = Matcher.Equals(argumentDeclaration)
                ).checked(),
            )

            fun metaIntroductionMatcher(
                resolvedIntroduction: Matcher<ResolvedIntroduction>,
            ): LevelResolvedIntroductionMatcher = LevelResolvedIntroductionMatcher(
                level = Matcher.Equals(StaticScope.Level.Meta),
                resolvedIntroduction = resolvedIntroduction,
            )
        }

        override fun match(actual: LeveledResolvedIntroduction) {
            level.match(actual.level)
            resolvedIntroduction.match(actual.resolvedIntroduction)
        }
    }
}
