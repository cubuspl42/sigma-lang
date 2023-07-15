package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.LocalValueDefinitionBlock
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val term: LetExpressionTerm,
    val definitionBlock: LocalValueDefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val (definitionBlock, innerDeclarationScope) = DeclarationScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalValueDefinitionBlock.build(
                    typeScope = typeScope,
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = term.localScope.definitions,
                )

                val innerDeclarationScope = definitionBlock.chainWith(
                    outerScope = outerDeclarationScope,
                )

                return@looped Pair(
                    definitionBlock,
                    innerDeclarationScope,
                )
            }

            return LetExpression(
                term = term,
                definitionBlock = definitionBlock,
                result = Expression.build(
                    typeScope = typeScope,
                    declarationScope = innerDeclarationScope,
                    term = term.result,
                ),
            )
        }
    }

    override val inferredType: Computation<Type>
        get() = result.inferredType

    override val errors: Set<SemanticError> by lazy {
        definitionBlock.errors + result.errors
    }

    override fun evaluate(
        scope: Scope,
    ): Value = result.evaluate(
        scope = definitionBlock.evaluate(
            scope = scope,
        ),
    )
}
