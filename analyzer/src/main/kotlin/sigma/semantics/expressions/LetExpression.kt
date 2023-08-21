package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.LocalValueDefinitionBlock
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.Type
import sigma.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val term: LetExpressionTerm,
    val definitionBlock: LocalValueDefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val (definitionBlock, innerDeclarationScope) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalValueDefinitionBlock.build(
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
                    declarationScope = innerDeclarationScope,
                    term = term.result,
                ),
            )
        }
    }

    override val inferredType: Thunk<Type>
        get() = result.inferredType

    override fun bind(scope: Scope): Thunk<Value> = result.bind(
        scope = definitionBlock.evaluate(
            scope = scope,
        ),
    )

    override val errors: Set<SemanticError> by lazy {
        definitionBlock.errors + result.errors
    }
}