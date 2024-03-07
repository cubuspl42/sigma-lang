package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTermTest {
    @Test
    fun testSimple() {
        val term = ModuleTerm.parse(
            source = """
                %import calculator
                
                %val result = calculator.calculate{}
            """.trimIndent()
        )

        assertEquals(
            expected = ModuleTerm(
                imports = listOf(
                    ModuleTerm.ImportTerm(
                        importedModuleName = IdentifierTerm(name = "calculator")
                    )
                ),
                definitions = listOf(
                    ModuleTerm.ValueDefinitionTerm(
                        name = IdentifierTerm(name = "result"),
                        initializer = CallTerm(
                            callee = FieldReadTerm(
                                subject = ReferenceTerm(
                                    referredName = IdentifierTerm(name = "calculator")
                                ),
                                readFieldName = IdentifierTerm(name = "calculate"),
                            ),
                            passedArgument = UnorderedTupleConstructorTerm.Empty,
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}
