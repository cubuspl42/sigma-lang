package sigma.semantics.expressions

import sigma.semantics.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedCheckTests {
    object TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
                source = "isUndefined foo",
            ) as IsUndefinedCheckTerm

            val isUndefinedCheck = IsUndefinedCheck.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            assertEquals(
                expected = BoolType,
                actual = isUndefinedCheck.inferredType.value,
            )
        }
    }
}
