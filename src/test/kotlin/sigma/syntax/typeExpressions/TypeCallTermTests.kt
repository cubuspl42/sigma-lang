package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.GenericTypeConstructor
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import utils.FakeDeclarationBlock
import utils.FakeTypeEntityDefinition
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeCallTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = TypeExpressionTerm.parse(
                source = "Foo[Bar, {a: Int, b: Bool}]",
            )

            assertEquals(
                expected = TypeCallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    callee = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("Foo"),
                    ),
                    passedArgument = TypeCallTerm.TypeTupleConstructor(
                        elements = listOf(
                            TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("Bar"),
                            ),
                            UnorderedTupleTypeConstructorTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                entries = listOf(
                                    UnorderedTupleTypeConstructorTerm.Entry(
                                        name = Symbol.of("a"),
                                        valueType = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                    UnorderedTupleTypeConstructorTerm.Entry(
                                        name = Symbol.of("b"),
                                        valueType = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 21),
                                            referee = Symbol.of("Bool"),
                                        ),
                                    ),
                                ),
                            )
                        ),
                    ),
                ),
                actual = term,
            )
        }
    }

    object EvaluationTests {
        @Test
        @Ignore
        fun test() {
            val genericTypeConstructor = TypeExpressionTerm.parse(
                source = "![A, B] {a: A, b: B, c: Int}",
            ).evaluate(
                declarationScope = BuiltinScope,
            ) as GenericTypeConstructor

            val callTerm = TypeExpressionTerm.parse(
                source = "Foo[Int, Bool]",
            ) as TypeCallTerm

            val resultEntity = callTerm.evaluate(
                declarationScope = FakeDeclarationBlock(
                    declarations = setOf(
                        FakeTypeEntityDefinition(
                            name = Symbol.of("Foo"),
                            definedTypeEntity = genericTypeConstructor,
                        ),
                    ),
                ).chainWith(
                    outerScope = BuiltinScope,
                ),
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("a") to IntCollectiveType,
                        Symbol.of("b") to BoolType,
                        Symbol.of("c") to IntCollectiveType,
                    ),
                ),
                actual = resultEntity,
            )
        }
    }
}
