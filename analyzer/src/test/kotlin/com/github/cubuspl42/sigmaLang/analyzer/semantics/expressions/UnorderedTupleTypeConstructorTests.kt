package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import utils.assertTypeIsEquivalent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class UnorderedTupleTypeConstructorTests {
    @Test
    fun testEmpty() {
        val term = ExpressionSourceTerm.parse(
            source = "^{}",
        ) as UnorderedTupleTypeConstructorSourceTerm

        val unorderedTupleTypeConstructor = UnorderedTupleTypeConstructor.build(
            context = Expression.BuildContext.Empty,
            term = term,
        ).resolved

        assertEquals(
            expected = TypeType,
            actual = unorderedTupleTypeConstructor.inferredTypeOrIllType.getOrCompute(),
        )

        val actualType = assertNotNull(
            actual = unorderedTupleTypeConstructor.bind(dynamicScope = DynamicScope.Empty).value?.asType,
        ) as MembershipType

        assertTypeIsEquivalent(
            expected = UnorderedTupleType(
                valueTypeByName = emptyMap(),
            ),
            actual = actualType,
        )
    }

    @Test
    fun testSimple() {
        val term = ExpressionSourceTerm.parse(
            source = "^{k1: Int, k2: Bool}",
        ) as UnorderedTupleTypeConstructorSourceTerm

        val unorderedTupleTypeConstructor = UnorderedTupleTypeConstructor.build(
            context = Expression.BuildContext.Builtin,
            term = term,
        ).resolved

        assertEquals(
            expected = emptySet(),
            actual = unorderedTupleTypeConstructor.errors,
        )

        assertEquals(
            expected = TypeType,
            actual = unorderedTupleTypeConstructor.inferredTypeOrIllType.getOrCompute(),
        )

        val valueClassification = assertIs<ConstExpression>(
            unorderedTupleTypeConstructor.classified
        )

        val expectedType = UnorderedTupleType(
            valueTypeByName = mapOf(
                Identifier.of("k1") to IntCollectiveType,
                Identifier.of("k2") to BoolType,
            ),
        )

        val classifiedType = assertNotNull(valueClassification.valueThunk.value?.asType) as MembershipType

        assertTypeIsEquivalent(
            expected = expectedType,
            actual = classifiedType,
        )

        val evaluatedType = assertNotNull(
            unorderedTupleTypeConstructor.bind(
                dynamicScope = DynamicScope.Empty,
            ).value?.asType,
        ) as MembershipType

        assertTypeIsEquivalent(
            expected = expectedType,
            actual = evaluatedType,
        )
    }

    @Test
    fun testRecursive() {
        val term = ExpressionSourceTerm.parse(
            source = """
                %let {
                    Entry = ^{
                        k1: Int,
                        k2: Entry,
                    },
                } %in Entry
            """.trimIndent(),
        ) as LetExpressionTerm

        val letExpression = LetExpression.build(
            context = Expression.BuildContext.Builtin,
            term = term,
        )

        val result = letExpression.resultStub.resolved

        assertEquals(
            expected = TypeType,
            actual = result.inferredTypeOrIllType.getOrCompute(),
        )

        assertEquals(
            expected = emptySet(),
            actual = result.errors,
        )

        val actualType = assertNotNull(
            actual = result.bind(
                dynamicScope = DynamicScope.Empty,
            ).value?.asType,
        ) as MembershipType

        assertTypeIsEquivalent(
            expected = object : UnorderedTupleType() {
                override val valueTypeThunkByName = mapOf(
                    Identifier.of("k1") as Symbol to Thunk.pure(IntCollectiveType),
                    Identifier.of("k2") as Symbol to Thunk.pure(this),
                )
            },
            actual = actualType,
        )
    }
}
