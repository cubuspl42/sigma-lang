package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinScope
import sigma.semantics.types.GenericTypeConstructor
import sigma.semantics.types.TypeVariable
import sigma.syntax.expressions.GenericParametersTuple
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenericTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = TypeExpressionTerm.parse(
                source = "![A, B] ^{a: A, b: B, c: Int}",
            )

            assertEquals(
                expected = GenericTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        parametersDefinitions = listOf(
                            GenericParametersTuple.GenericParameterDefinition(
                                name = Symbol.of("A"),
                                definedTypeVariable = TypeVariable(
                                    name =  Symbol.of("A"),
                                ),
                            ),
                            GenericParametersTuple.GenericParameterDefinition(
                                name = Symbol.of("B"),
                                definedTypeVariable = TypeVariable(
                                    name =  Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    body = UnorderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        entries = listOf(
                            UnorderedTupleTypeConstructorTerm.Entry(
                                name = Symbol.of(name = "a"),
                                valueType = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                    referee = Symbol.of(name = "A"),
                                ),
                            ),
                            UnorderedTupleTypeConstructorTerm.Entry(
                                name = Symbol.of(name = "b"),
                                valueType = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                    referee = Symbol.of(name = "B"),
                                ),
                            ),
                            UnorderedTupleTypeConstructorTerm.Entry(
                                name = Symbol.of(name = "c"),
                                valueType = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 25),
                                    referee = Symbol.of(name = "Int"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = term,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val term = TypeExpressionTerm.parse(
                source = "![A, B] ^{a: A, b: B, c: Int}",
            )

            val typeEntity = term.evaluate(declarationScope = BuiltinScope)

            assertIs<GenericTypeConstructor>(typeEntity)
        }
    }
}
