@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.evaluation.values.DictValueMatchers
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ParametricType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TypeSpecificationTerm
import utils.CollectionMatchers
import utils.FakeArgumentDeclarationBlock
import utils.FakeDefinition
import utils.FakeDefinitionBlock
import utils.FakeStaticScope
import utils.FakeUserDeclaration
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test

class TypeSpecificationTests {
    companion object {
        /**
         * ```
         * !^[t1: Type, t2: Type] !=> ^{x: t1, y: t2}
         * ```
         */
        private val genericType1 = object : ParametricType() {
            override fun parametrize(metaArgument: DictValue): Type {
                val t1 = metaArgument.read(IntValue(value = 0L))!!.thenJust { it.asType!! }
                val t2 = metaArgument.read(IntValue(value = 1L))!!.thenJust { it.asType!! }

                return UnorderedTupleType.fromEntries(
                    entries = setOf(
                        UnorderedTupleType.NamedEntry(
                            name = Identifier.of("x"),
                            typeThunk = t1,
                        ),
                        UnorderedTupleType.NamedEntry(
                            name = Identifier.of("y"),
                            typeThunk = t2,
                        ),
                    )
                )
            }

            override val parameterType: TupleType = OrderedTupleType.of(
                TypeType,
                TypeType,
            )
        }
    }

    class ConstructionTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "f![a, b]",
            ) as TypeSpecificationTerm

            val typeSpecification = TypeSpecification.build(
                context = Expression.BuildContext(
                    outerScope = FakeArgumentDeclarationBlock(
                        declarations = setOf(
                            FakeUserDeclaration(
                                name = Identifier.of("f"),
                                declaredType = genericType1,
                            ),
                        ),
                    ).chainWith(
                        FakeDefinitionBlock(
                            level = StaticScope.Level.Meta,
                            definitions = setOf(
                                FakeDefinition(
                                    name = Identifier.of("a"),
                                    type = TypeType,
                                    value = IntCollectiveType.asValue,
                                ),
                                FakeDefinition(
                                    name = Identifier.of("b"),
                                    type = TypeType,
                                    value = BoolType.asValue,
                                ),
                            ),
                        )
                    ),
                ),
                term = term,
            ).value

            assertMatches(
                matcher = TypeSpecificationMatcher(
                    subject = Matcher.Irrelevant(),
                    metaArgument = DictValueMatchers.eachOnce(
                        entries = setOf(
                            DictValueMatchers.EntryMatcher(
                                index = 0L,
                                value = Matcher.Equals(IntCollectiveType.asValue),
                            ),
                            DictValueMatchers.EntryMatcher(
                                index = 1L,
                                value = Matcher.Equals(BoolType.asValue),
                            ),
                        )
                    ),
                ),
                actual = typeSpecification,
            )
        }
    }

    class TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "g![p, q]",
            ) as TypeSpecificationTerm

            val typeSpecification = TypeSpecification.build(
                context = Expression.BuildContext(
                    outerScope = FakeArgumentDeclarationBlock(
                        declarations = setOf(
                            FakeUserDeclaration(
                                name = Identifier.of("g"),
                                declaredType = genericType1,
                            ),
                        ),
                    ).chainWith(
                        FakeDefinitionBlock(
                            level = StaticScope.Level.Meta,
                            definitions = setOf(
                                FakeDefinition(
                                    name = Identifier.of("p"),
                                    type = TypeType,
                                    value = IntCollectiveType.asValue,
                                ),
                                FakeDefinition(
                                    name = Identifier.of("q"),
                                    type = TypeType,
                                    value = BoolType.asValue,
                                ),
                            ),
                        ),
                    ),
                ),
                term = term,
            ).value

            assertMatches(
                matcher = UnorderedTupleTypeMatcher(
                    entries = CollectionMatchers.eachOnce(
                        setOf(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("x")),
                                type = Matcher.Equals(IntCollectiveType),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("y")),
                                type = Matcher.Equals(BoolType),
                            ),
                        )
                    )
                ).checked(),
                actual = typeSpecification.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {
        }
    }
}
