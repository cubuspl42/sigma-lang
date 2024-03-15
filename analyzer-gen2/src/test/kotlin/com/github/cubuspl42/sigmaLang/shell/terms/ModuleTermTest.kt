package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.StringValue
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

    @Test
    fun testAliasImport() {
        val term = ModuleTerm.parse(
            source = """
                %import calculator %as calc
                
                %val a = ""
            """.trimIndent()
        )

        assertEquals(
            expected = ModuleTerm(
                imports = listOf(
                    ModuleTerm.ImportTerm(
                        importedModuleName = IdentifierTerm(name = "calculator"),
                        aliasName = IdentifierTerm(name = "calc"),
                    )
                ),
                definitions = listOf(
                    ModuleTerm.ValueDefinitionTerm(
                        name = IdentifierTerm(name = "a"),
                        initializer = StringLiteralTerm(
                            value = StringValue(value = ""),
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}
