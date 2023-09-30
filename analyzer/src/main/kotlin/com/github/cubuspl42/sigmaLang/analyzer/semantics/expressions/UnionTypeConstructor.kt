package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm

class UnionTypeConstructor(
    override val outerScope: StaticScope,
    override val term: UnionTypeConstructorTerm,
    val types: Set<Expression>,
) : TypeConstructor() {
    init {
        if (types.isEmpty()) {
            throw IllegalArgumentException("Union has to consist of at least one type")
        }
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: UnionTypeConstructorTerm,
        ): UnionTypeConstructor = UnionTypeConstructor(
            outerScope = outerScope,
            term = term,
            types = buildTypes(
                outerScope = outerScope,
                accumulatedTypes = emptySet(),
                term = term,
            ),
        )

        private fun buildTypes(
            outerScope: StaticScope,
            accumulatedTypes: Set<Expression>,
            term: UnionTypeConstructorTerm,
        ): Set<Expression> {
            val leftTypeTerm = term.leftType
            val rightTypeTerm = term.rightType

            if (rightTypeTerm is UnionTypeConstructorTerm) {
                throw IllegalArgumentException("Unexpected union type term structure")
            }

            val rightType = Expression.build(
                outerScope = outerScope,
                term = rightTypeTerm,
            )

            val extendedTypes = accumulatedTypes + rightType

            return if (leftTypeTerm is UnionTypeConstructorTerm) {
                buildTypes(
                    outerScope = outerScope,
                    accumulatedTypes = extendedTypes,
                    term = leftTypeTerm,
                )
            } else {
                val leftType = Expression.build(
                    outerScope = outerScope,
                    term = leftTypeTerm,
                )

                extendedTypes + leftType
            }
        }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(types.toList()) { typeExpression ->
        typeExpression.bind(dynamicScope = dynamicScope).thenJust { type ->
            type.asType!!
        }
    }.thenJust { types ->
        UnionType(
            memberTypes = types.toSet(),
        ).asValue
    }

    override val subExpressions: Set<Expression> = types
}
