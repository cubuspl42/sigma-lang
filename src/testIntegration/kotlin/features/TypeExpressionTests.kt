package features

import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Symbol
import sigma.semantics.Namespace
import sigma.semantics.Prelude
import sigma.semantics.types.BoolType
import sigma.semantics.types.MetaType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.NamespaceDefinitionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeExpressionTests {
    @Test
    fun testUnresolved() {
        // Test that a type expression which references non-existing entities
        // results in a semantic error
    }
}
