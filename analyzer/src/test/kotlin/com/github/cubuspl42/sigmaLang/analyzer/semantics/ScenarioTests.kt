package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.CallMatchers
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import utils.CollectionMatchers
import utils.Matcher
import utils.assertMatches
import utils.assertTypeIsEquivalent
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ScenarioTests {
    @Test
    @Ignore // TODO: Re-support type aliases
    fun testGenericClass() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const Entry = ^[valueType: Type] => ^{
                        key: Int,
                        value: valueType,
                    }
                    
                    %const entryOf = !^[valueType: Type] ^{
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

        val namespaceDefinition = NamespaceDefinition.build(
            context = Expression.BuildContext(
                outerMetaScope = BuiltinScope,
                outerScope = Project.loadPrelude().innerStaticScope,
            ),
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespaceDefinition.errors,
        )

        // Validate `Entry`

        val entryTypeConstructorDefinition = namespaceDefinition.getDefinition(
            name = Identifier.of("Entry"),
        )!!

        assertEquals(
            expected = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Identifier.of("valueType"),
                            type = TypeType,
                        ),
                    ),
                ),
                imageType = TypeType,
            ),
            actual = entryTypeConstructorDefinition.computedBodyType.getOrCompute(),
        )

        // Construct `Entry[Bool]` and validate it

        val entryTypeConstructorValue = entryTypeConstructorDefinition.valueThunk.value

        assertIs<FunctionValue>(entryTypeConstructorValue)

        val entryType = assertNotNull(
            actual = entryTypeConstructorValue.apply(
                ArrayTable(
                    elements = listOf(
                        BoolType.asValue,
                    ),
                ),
            ).value?.asType
        ) as MembershipType

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

        val entryOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Identifier.of("entryOf"),
        )!!

        val valueTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("valueType"),
        )

        assertTypeIsEquivalent(
            expected = UniversalFunctionType(
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key") to IntCollectiveType,
                        Identifier.of("value") to valueTypeDefinition.typePlaceholder,
                    ),
                ),
                imageType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key") to IntCollectiveType,
                        Identifier.of("value") to valueTypeDefinition.typePlaceholder,
                    ),
                ),
            ),
            actual = entryOfAbstractionDefinition.computedBodyType.getOrCompute() as MembershipType,
        )

        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Identifier.of("entryTrueOf"),
        )!!

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
            actual = entryTrueOfAbstractionDefinition.computedBodyType.getOrCompute() as MembershipType,
        )
    }

    @Test
    fun testNonInferableGenericFunctionCall() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const f = !^[type: Type] ^[] -> ^[type...] => 0
                    
                    %const a = f[]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            context = Expression.BuildContext.Builtin,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        val error = assertIs<Call.NonFullyInferredCalleeTypeError>(
            namespaceDefinition.errors.singleOrNull(),
        )

        assertMatches(
            matcher = CallMatchers.NonFullyInferredCalleeTypeErrorMatcher(
                calleeGenericType = Matcher.Is<UniversalFunctionType>(),
                unresolvedPlaceholders = CollectionMatchers.eachOnce(
                    elements = setOf(
                        Matcher.Is<TypePlaceholder>(),
                    ),
                ),
            ),
            actual = error,
        )

        val aType = namespaceDefinition.getDefinition(
            name = Identifier.of("a"),
        )!!.computedBodyType.getOrCompute()

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
                    %const f = !^[aType: Type] ^[a: aType] -> Int => %let {
                        g = !^[bType: Type, cType: Type] ^[a: aType, b: bType, c: cType] -> Int => 0,
                    } %in g[a, false, {}]
                    
                    %const a = f[1]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            context = Expression.BuildContext(
                outerMetaScope = BuiltinScope,
                outerScope = StaticScope.Empty,
            ),
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespaceDefinition.errors,
        )
    }
}
