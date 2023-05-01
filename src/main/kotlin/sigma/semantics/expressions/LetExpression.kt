package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.DefinitionBlock
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val term: LetExpressionTerm,
    val definitionBlock: DefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val definitionBlock = DefinitionBlock.build(
                typeScope = typeScope,
                outerDeclarationScope = outerDeclarationScope,
                declarations = term.localScope.declarations,
            )

            return LetExpression(
                term = term,
                definitionBlock = definitionBlock,
                result = Expression.build(
                    typeScope = typeScope,
                    declarationScope = definitionBlock,
                    term = term.result,
                ),
            )
        }
    }

    override val inferredType: Computation<Type>
        get() = result.inferredType

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
