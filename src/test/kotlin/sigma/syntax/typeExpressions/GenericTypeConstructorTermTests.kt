package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinScope
import sigma.semantics.types.GenericTypeConstructor
import sigma.semantics.types.TypeVariable
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.GenericParametersTuple
import sigma.syntax.expressions.GenericTypeConstructorTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenericTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
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
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of(name = "a"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                    referee = Symbol.of(name = "A"),
                                ),
                            ),
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of(name = "b"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                    referee = Symbol.of(name = "B"),
                                ),
                            ),
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of(name = "c"),
                                value = ReferenceTerm(
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
//            val term = ExpressionTerm.parse(
//                source = "![A, B] ^{a: A, b: B, c: Int}",
//            )
//
//            val typeEntity = term.evaluate(declarationScope = BuiltinScope)
//
//            assertIs<GenericTypeConstructor>(typeEntity)
        }
    }
}
