package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import utils.assertTypeIsEquivalent
import utils.assertTypeIsNonEquivalent
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeTests {
    class MatchTests {
        @Test
        fun testAssignedSameTupleType() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            assertEquals(
                expected = true,
                actual = unorderedTupleType.match(
                    assignedType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("foo") to IntCollectiveType,
                            Identifier.of("bar") to BoolType,
                        )
                    ),
                ).isFull(),
            )
        }

        @Test
        fun testAssignedUnionTypeOfOverlappingTupleTypes() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            val overlappingType1 = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("ordered") to OrderedTupleType.of(StringType),
                    Identifier.of("bar") to BoolType,
                    Identifier.of("foo") to IntLiteralType.of(value = 2L),
                )
            )

            val overlappingType2 = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("baz") to StringType,
                    Identifier.of("bar") to BoolType,
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
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            val overlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("ordered") to OrderedTupleType.of(StringType),
                    Identifier.of("bar") to BoolType,
                    Identifier.of("foo") to IntLiteralType.of(value = 2L),
                )
            )

            val nonOverlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("baz") to StringType,
                    Identifier.of("bar") to BoolType,
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
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            val overlappingType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("bar") to BoolType,
                    Identifier.of("foo") to IntLiteralType.of(value = 2L),
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

    class IsEquivalentTests {
        @Test
        fun testAcyclicEquivalent() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("foo") to IntCollectiveType,
                        Identifier.of("bar") to BoolType,
                    )
                ),
                actual = unorderedTupleType,
            )
        }

        @Test
        fun testAcyclicNonEquivalent() {
            val unorderedTupleType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("foo") to IntCollectiveType,
                    Identifier.of("bar") to BoolType,
                )
            )

            assertTypeIsNonEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("foo") to IntCollectiveType,
                        Identifier.of("bar") to StringType,
                    )
                ),
                actual = unorderedTupleType,
            )
        }

        @Test
        fun testCyclicSimpleEquivalent() {
            val unorderedTupleType = object : UnorderedTupleType() {
                override val valueTypeThunkByName = mapOf(
                    Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                    Identifier.of("bar") to Thunk.pure(this),
                )
            }

            assertTypeIsEquivalent(
                expected = object : UnorderedTupleType() {
                    override val valueTypeThunkByName = mapOf(
                        Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                        Identifier.of("bar") to Thunk.pure(this),
                    )
                },
                actual = unorderedTupleType,
            )
        }

        @Test
        fun testCyclicComplexEquivalent() {
            val unorderedTupleType = object : UnorderedTupleType() {
                private val outer = this

                override val valueTypeThunkByName = mapOf(
                    Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                    Identifier.of("bar") to Thunk.pure(
                        object : UnorderedTupleType() {
                            override val valueTypeThunkByName = mapOf(
                                Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                                Identifier.of("bar") to Thunk.pure(outer),
                            )
                        },
                    ),
                )
            }

            assertTypeIsEquivalent(
                expected = object : UnorderedTupleType() {
                    override val valueTypeThunkByName = mapOf(
                        Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                        Identifier.of("bar") to Thunk.pure(this),
                    )
                },
                actual = unorderedTupleType,
            )
        }

        @Test
        fun testCyclicComplexNonEquivalent() {
            val unorderedTupleType = object : UnorderedTupleType() {
                private val outer = this

                override val valueTypeThunkByName = mapOf(
                    Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                    Identifier.of("bar") to Thunk.pure(
                        object : UnorderedTupleType() {
                            override val valueTypeThunkByName = mapOf(
                                Identifier.of("foo") to Thunk.pure(BoolType),
                                Identifier.of("bar") to Thunk.pure(outer),
                            )
                        },
                    ),
                )
            }

            assertTypeIsNonEquivalent(
                expected = object : UnorderedTupleType() {
                    override val valueTypeThunkByName = mapOf(
                        Identifier.of("foo") to Thunk.pure(IntCollectiveType),
                        Identifier.of("bar") to Thunk.pure(this),
                    )
                },
                actual = unorderedTupleType,
            )
        }
    }
}
