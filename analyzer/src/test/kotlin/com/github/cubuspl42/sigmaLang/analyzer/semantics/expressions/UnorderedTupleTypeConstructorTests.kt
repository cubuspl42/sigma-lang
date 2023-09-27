package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

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

        assertEquals(
            expected = UnorderedTupleType(
                valueTypeByName = emptyMap(),
            ),
            actual = unorderedTupleTypeConstructor.bind(dynamicScope = DynamicScope.Empty).value?.asType,
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

        assertEquals(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("k1") to IntCollectiveType,
                    Symbol.of("k2") to BoolType,
                ),
            ),
            actual = unorderedTupleTypeConstructor.bind(dynamicScope = BuiltinScope).value?.asType,
        )
    }
}
