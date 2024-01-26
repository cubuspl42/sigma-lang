package com.github.cubuspl42.sigmaLang.analyzer.semantics

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.CallMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.GenericTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.resolveName
import utils.CollectionMatchers
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.assertTypeIsEquivalent
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ScenarioTests {
    @Test
    fun testManualGenericClass() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %meta Entry = ^[valueType: Type] => ^{
                        key: Int,
                        value: valueType,
                    }
                    
                    %const entryOf = ^[valueType: Type] !=> ^{
                        key: Int,
                        value: valueType,
                    } -> Entry[valueType] => {
                        key: key,
                        value: value,
                    }
                    
                    %const entryTrueOf = ^{
                        key: Int,
                    } -> Entry[Bool] => {
                        key: key,
                        value: true,
                    }
                )
            """.trimIndent(),
        )

        val namespaceBuildOutput = NamespaceDefinition.analyze(
            context = Expression.BuildContext(
                outerScope = Project.loadPrelude().innerStaticScope,
            ),
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        val definitionBlock = namespaceBuildOutput.definitionBlock
        val namespaceBody = namespaceBuildOutput.namespaceBody

        assertEquals(
            expected = emptySet(),
            actual = namespaceBody.errors,
        )

        // Validate `Entry`

        val entryTypeConstructorDefinition = definitionBlock.resolveName(
            name = Identifier.of("Entry"),
        ) as ResolvedDefinition

        assertMatches(
            matcher = UniversalFunctionTypeMatcher(
                argumentType = OrderedTupleTypeMatcher(
                    elements = ListMatchers.inOrder(
                        OrderedTupleTypeMatcher.ElementMatcher(
                            name = Matcher.Equals(Identifier.of("valueType")),
                            type = Matcher.Is<TypeType>(),
                        ),
                    ),
                ).checked(),
                imageType = Matcher.Is<TypeType>(),
            ).checked(),
            actual = entryTypeConstructorDefinition.body.inferredTypeOrIllType.getOrCompute(),
        )

        // Construct `Entry[Bool]` and validate it

        val entryTypeConstructorValue = entryTypeConstructorDefinition.body.constClassified?.value

        assertIs<FunctionValue>(entryTypeConstructorValue)

        val entryType = assertNotNull(
            actual = entryTypeConstructorValue.apply(
                ArrayTable(
                    elements = listOf(
                        BoolType.asValue,
                    ),
                ),
            ).value?.asType
        ) as SpecificType

        assertTypeIsEquivalent(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("key") to IntCollectiveType,
                    Identifier.of("value") to BoolType,
                ),
            ),
            actual = entryType,
        )

        // Validate `entryOf`

        val entryOfAbstractionDefinition = definitionBlock.resolveName(
            name = Identifier.of("entryOf"),
        ) as ResolvedDefinition

        assertMatches(
            matcher = GenericTypeMatcher(
                parameterType = OrderedTupleTypeMatcher(
                    elements = ListMatchers.inOrder(
                        OrderedTupleTypeMatcher.ElementMatcher(
                            name = Matcher.Equals(Identifier.of("valueType")),
                            type = Matcher.Is<TypeType>(),
                        ),
                    ),
                ).checked(),
                bodyType = UniversalFunctionTypeMatcher(
                    argumentType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("key")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("value")),
                                type = Matcher.Is<TypeVariable>(),
                            ),
                        ),
                    ).checked(),
                    imageType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("key")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("value")),
                                type = Matcher.Is<TypeVariable>(),
                            ),
                        ),
                    ).checked(),
                ).checked(),
            ).checked(),
            actual = entryOfAbstractionDefinition.body.inferredTypeOrIllType.getOrCompute() as Type,
        )


        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = definitionBlock.resolveName(
            name = Identifier.of("entryTrueOf"),
        ) as ResolvedDefinition

        assertTypeIsEquivalent(
            expected = UniversalFunctionType(
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key") to IntCollectiveType,
                    ),
                ),
                imageType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key") to IntCollectiveType,
                        Identifier.of("value") to BoolType,
                    ),
                ),
            ),
            actual = entryTrueOfAbstractionDefinition.body.inferredTypeOrIllType.getOrCompute() as SpecificType,
        )
    }

    @Test
    fun testNonInferableGenericFunctionCall() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const f = ^[type: Type] !=> ^[] -> ^[type...] => 0
                    
                    %const a = f[]
                )
            """.trimIndent(),
        )

        val namespaceBuildOutput = NamespaceDefinition.analyze(
            context = Expression.BuildContext.Builtin,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        val definitionBlock = namespaceBuildOutput.definitionBlock
        val namespaceBody = namespaceBuildOutput.namespaceBody

        val error = assertIs<Call.NonFullyInferredCalleeTypeError>(
            namespaceBody.errors.singleOrNull(),
        )

        assertMatches(
            matcher = CallMatcher.NonFullyInferredCalleeTypeErrorMatcher(
                calleeGenericType = Matcher.Is<UniversalFunctionType>(),
                unresolvedPlaceholders = CollectionMatchers.eachOnce(
                    elements = setOf(
                        Matcher.Is<TypePlaceholder>(),
                    ),
                ),
            ),
            actual = error,
        )

        val aDefinition = definitionBlock.resolveName(
            name = Identifier.of("a"),
        ) as ResolvedDefinition

        val aType = aDefinition.body.inferredTypeOrIllType.getOrCompute()

        assertEquals(
            expected = IllType,
            actual = aType,
        )
    }

    @Test
    fun testNestedGenericFunctions() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const f = ^[aType: Type] !=> ^[a: aType] -> Int => %let {
                        g = ^[bType: Type, cType: Type] !=> ^[a: aType, b: bType, c: cType] -> Int => 0,
                    } %in g[a, false, {}]
                    
                    %const a = f[1]
                )
            """.trimIndent(),
        )

        val namespaceBody = NamespaceDefinition.analyze(
            context = Expression.BuildContext(
                outerScope = BuiltinScope,
            ),
            qualifiedPath = QualifiedPath.Root,
            term = term,
        ).namespaceBody

        assertEquals(
            expected = emptySet(),
            actual = namespaceBody.errors,
        )
    }
}
