package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class UnionTypeTests {
    class MatchTests {
        @Test
        fun testAssignedContainedType() {
            val unionType = UnionType(
                memberTypes = setOf(
                    IntCollectiveType,
                    BoolType,
                )
            )

            assertEquals(
                expected = MembershipType.TotalMatch,
                actual = unionType.match(
                    assignedType = IntCollectiveType,
                ),
            )
        }

        @Test
        fun testAssignedUnrelatedType() {
            val unionType = UnionType(
                memberTypes = setOf(
                    IntCollectiveType,
                    BoolType,
                )
            )

            val assignedType = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = UnionType.UnionMatch(
                    expectedType = unionType,
                    unmatchedTypes = setOf(assignedType),
                ),
                actual = unionType.match(
                    assignedType = assignedType,
                ),
            )
        }

        @Test
        fun testAssignedContainedSubtype() {
            val unionType = UnionType(
                memberTypes = setOf(
                    IntCollectiveType,
                    BoolType,
                )
            )

            assertEquals(
                expected = MembershipType.TotalMatch,
                actual = unionType.match(
                    assignedType = IntLiteralType(
                        value = IntValue.Zero,
                    ),
                ),
            )
        }

        @Test
        fun testAssignedOverlappingUnionType() {
            val missingType = BoolType

            val overlappingType1 = IntCollectiveType

            val overlappingType2 = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("a") to IntCollectiveType,
                ),
            )

            val overlappingType2Subtype = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("a") to IntLiteralType.of(value = 1L),
                ),
            )

            val nonOverlappingType1 = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = IntCollectiveType,
                    ),
                ),
            )

            val nonOverlappingType2 = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = BoolType,
                    ),
                ),
            )

            val unionType = UnionType(
                memberTypes = setOf(
                    missingType,
                    overlappingType1,
                    overlappingType2,
                ),
            )

            assertEquals(
                expected = UnionType.UnionMatch(
                    expectedType = unionType,
                    unmatchedTypes = setOf(
                        nonOverlappingType2,
                        nonOverlappingType1,
                    ),
                ),
                actual = unionType.match(
                    assignedType = UnionType(
                        memberTypes = setOf(
                            overlappingType2Subtype,
                            nonOverlappingType1,
                            overlappingType1,
                            nonOverlappingType2,
                        ),
                    ),
                ),
            )
        }
    }
}
