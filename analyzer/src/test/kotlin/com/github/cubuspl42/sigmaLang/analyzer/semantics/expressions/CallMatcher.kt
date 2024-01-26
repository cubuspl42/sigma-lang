package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.BinaryOperator
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.resolveName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import utils.Matcher
import utils.checked

class CallMatcher(
    private val subject: Matcher<Expression>,
    private val argument: Matcher<Expression>,
) : Matcher<Call>() {
    companion object {
        fun arrayIndex(
            array: Matcher<Expression>,
            index: Long,
        ): Matcher<Call> = CallMatcher(
            subject = array,
            argument = IntLiteralMatcher(
                value = Matcher.Equals(index),
            ).checked(),
        )

        fun infix(
            operator: BinaryOperator,
            leftArgument: Matcher<Expression>,
            rightArgument: Matcher<Expression>,
        ): Matcher<Call> {
            val operatorDefinition =
                BuiltinScope.resolveName(name = operator.functionNameIdentifier) as ResolvedDefinition

            return CallMatcher(
                subject = Matcher.Equals(operatorDefinition.body),
                argument = UnorderedTupleConstructorMatcher.withEachEntryOnce(
                    UnorderedTupleConstructorMatcher.EntryMatcher(
                        name = Matcher.Equals(operator.leftArgument),
                        value = leftArgument,
                    ),
                    UnorderedTupleConstructorMatcher.EntryMatcher(
                        name = Matcher.Equals(operator.rightArgument),
                        value = rightArgument,
                    ),
                ).checked(),
            )
        }
    }

    class NonFullyInferredCalleeTypeErrorMatcher(
        val calleeGenericType: Matcher<TypeAlike>,
        val unresolvedPlaceholders: Matcher<Set<TypeAlike>>,
    ) : Matcher<Call.NonFullyInferredCalleeTypeError>() {
        override fun match(actual: Call.NonFullyInferredCalleeTypeError) {
            calleeGenericType.match(actual = actual.calleeGenericType)
            unresolvedPlaceholders.match(actual = actual.unresolvedPlaceholders)
        }
    }

    override fun match(actual: Call) {
        subject.match(actual = actual.subject)
        argument.match(actual = actual.argument)
    }
}
