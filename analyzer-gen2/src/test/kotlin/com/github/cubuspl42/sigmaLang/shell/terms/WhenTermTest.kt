package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class WhenTermTest {
    @Test
    fun testSimple() {
        val term = WhenTerm.parse(
            source = """
                %when (
                    %case someReference1 => someFunction2{arg1 = {}}
                    %case someFunction1{arg1 = {}} => someReference2
                    %else => someFunction3{arg1 = {}}
                )
            """.trimIndent()
        )

        assertEquals(
            expected = WhenTerm(
                caseBlocks = listOf(
                    WhenTerm.CaseBlock(
                        condition = ReferenceTerm(
                            referredName = IdentifierTerm(name = "someReference1"),
                        ),
                        result = CallTerm(
                            callee = ReferenceTerm(
                                referredName = IdentifierTerm(name = "someFunction2"),
                            ),
                            passedArgument = UnorderedTupleConstructorTerm(
                                entries = listOf(
                                    UnorderedTupleConstructorTerm.Entry(
                                        key = IdentifierTerm("arg1"),
                                        value = UnorderedTupleConstructorTerm.Empty,
                                    ),
                                ),
                            ),
                        ),
                    ),
                    WhenTerm.CaseBlock(
                        condition = CallTerm(
                            callee = ReferenceTerm(
                                referredName = IdentifierTerm(name = "someFunction1"),
                            ),
                            passedArgument = UnorderedTupleConstructorTerm(
                                entries = listOf(
                                    UnorderedTupleConstructorTerm.Entry(
                                        key = IdentifierTerm("arg1"),
                                        value = UnorderedTupleConstructorTerm.Empty,
                                    ),
                                ),
                            ),
                        ),
                        result = ReferenceTerm(
                            referredName = IdentifierTerm(name = "someReference2"),
                        ),
                    ),
                ),
                elseBlock = CallTerm(
                    callee = ReferenceTerm(
                        referredName = IdentifierTerm(name = "someFunction3"),
                    ),
                    passedArgument = UnorderedTupleConstructorTerm(
                        entries = listOf(
                            UnorderedTupleConstructorTerm.Entry(
                                key = IdentifierTerm("arg1"),
                                value = UnorderedTupleConstructorTerm.Empty,
                            ),
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}
