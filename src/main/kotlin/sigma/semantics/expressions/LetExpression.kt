package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.LocalDefinitionBlock
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val term: LetExpressionTerm,
    val definitionBlock: LocalDefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val (definitionBlock, innerDeclarationScope) = DeclarationScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalDefinitionBlock.build(
                    typeScope = typeScope,
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = term.localScope.declarations,
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
}
