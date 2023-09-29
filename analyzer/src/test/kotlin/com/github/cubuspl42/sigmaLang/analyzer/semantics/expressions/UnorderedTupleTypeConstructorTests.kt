package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UndefinedType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import utils.assertTypeIsEquivalent
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UnorderedTupleTypeConstructorTests {
    @Test
    fun testEmpty() {
        val term = ExpressionSourceTerm.parse(
            source = "^{}",
        ) as UnorderedTupleTypeConstructorSourceTerm

        val unorderedTupleTypeConstructor = UnorderedTupleTypeConstructor.build(
            outerScope = StaticScope.Empty,
            term = term,
        )

        assertEquals(
            expected = MetaType,
            actual = unorderedTupleTypeConstructor.inferredTypeOrIllType.getOrCompute(),
        )

        val actualType = assertNotNull(
            actual = unorderedTupleTypeConstructor.bind(dynamicScope = DynamicScope.Empty).value?.asType,
        )

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
            outerScope = BuiltinScope,
            term = term,
        )

        assertEquals(
            expected = MetaType,
            actual = unorderedTupleTypeConstructor.inferredTypeOrIllType.getOrCompute(),
        )

        val actualType = assertNotNull(
            actual = unorderedTupleTypeConstructor.bind(dynamicScope = BuiltinScope).value?.asType,
        )

        assertTypeIsEquivalent(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("k1") to IntCollectiveType,
                    Symbol.of("k2") to BoolType,
                ),
            ),
            actual = actualType,
        )
    }

    @Test
    @Ignore
    fun testRecursive() {
        val term = ExpressionSourceTerm.parse(
            source = """
                %let {
                    Entry = ^{
                        k1: Int,
                        k2: Entry | Undefined,
                    },
                } %in Entry
            """.trimIndent(),
        ) as LetExpressionTerm

        val letExpression = LetExpression.build(
            outerScope = BuiltinScope,
            term = term,
        )

        assertEquals(
            expected = MetaType,
            actual = letExpression.inferredTypeOrIllType.getOrCompute(),
        )

        assertEquals(
            expected = emptySet(),
            actual = letExpression.errors,
        )

        val actualType = assertNotNull(
            actual = letExpression.bind(dynamicScope = BuiltinScope).value?.asType,
        )

        assertTypeIsEquivalent(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("k1") to IntCollectiveType,
                    Symbol.of("k2") to /* Entry | */ UndefinedType, // TODO: Recursive types
                ),
            ),
            actual = actualType,
        )
    }
}
