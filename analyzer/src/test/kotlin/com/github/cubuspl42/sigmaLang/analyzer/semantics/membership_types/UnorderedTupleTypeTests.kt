package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeTests {
    class MatchTests {
        @Test
        fun testAssignedSameTupleType() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            assertEquals(
                expected = true,
                actual = unorderedTupleType.match(
                    assignedType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Symbol.of("foo") to IntCollectiveType,
                            Symbol.of("bar") to BoolType,
                        )
                    ),
                ).isFull(),
            )
        }

        @Test
        fun testAssignedUnionTypeOfOverlappingTupleTypes() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val overlappingType1 = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("ordered") to OrderedTupleType.of(StringType),
                    Symbol.of("bar") to BoolType,
                    Symbol.of("foo") to IntLiteralType.of(value = 2L),
                )
            )

            val overlappingType2 = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("baz") to StringType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val unionType = UnionType(
                memberTypes = setOf(
                    overlappingType1,
                    overlappingType2,
                )
            )

            assertEquals(
                expected = MembershipType.TotalMatch,
                actual = unorderedTupleType.match(
                    assignedType = unionType,
                ),
            )
        }

        @Test
        fun testAssignedUnionTypeOfPartiallyNonOverlappingTupleTypes() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val overlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("ordered") to OrderedTupleType.of(StringType),
                    Symbol.of("bar") to BoolType,
                    Symbol.of("foo") to IntLiteralType.of(value = 2L),
                )
            )

            val nonOverlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("baz") to StringType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val unionType = UnionType(
                memberTypes = setOf(
                    overlappingType,
                    nonOverlappingType,
                )
            )

            assertEquals(
                expected = UnionType.AssignedUnionMatch(
                    expectedType = unorderedTupleType,
                    nonMatchingTypes = setOf(nonOverlappingType),
                ),
                actual = unorderedTupleType.match(
                    assignedType = unionType,
                ),
            )
        }

        @Test
        fun testAssignedUnionTypeOfNonTupleTypes() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val overlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("bar") to BoolType,
                    Symbol.of("foo") to IntLiteralType.of(value = 2L),
                )
            )

            val nonTupleType = BoolType

            val unionType = UnionType(
                memberTypes = setOf(
                    overlappingType,
                    nonTupleType,
                )
            )

            assertEquals(
                expected = UnionType.AssignedUnionMatch(
                    expectedType = unorderedTupleType,
                    nonMatchingTypes = setOf(nonTupleType),
                ),
                actual = unorderedTupleType.match(
                    assignedType = unionType,
                ),
            )
        }
    }
}
