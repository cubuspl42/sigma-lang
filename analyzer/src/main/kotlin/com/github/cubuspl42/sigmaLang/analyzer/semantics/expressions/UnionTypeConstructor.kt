package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm

abstract class UnionTypeConstructor : TypeConstructor() {
    abstract override val term: UnionTypeConstructorTerm

    abstract val types: Set<Expression>

    companion object {
        fun build(
            context: BuildContext,
            term: UnionTypeConstructorTerm,
        ): Stub<UnionTypeConstructor> {
            val typeStubs = buildTypes(
                context = context,
                accumulatedTypes = emptySet(),
                term = term,
            )

            if (typeStubs.isEmpty()) {
                throw IllegalArgumentException("Union has to consist of at least one type")
            }

            return object : Stub<UnionTypeConstructor> {
                override val resolved: UnionTypeConstructor by lazy {
                    object : UnionTypeConstructor() {
                        override val outerScope: StaticScope = context.outerScope

                        override val term: UnionTypeConstructorTerm = term

                        override val types: Set<Expression> by lazy {
                            typeStubs.map { it.resolved }.toSet()
                        }
                    }
                }
            }
        }

        private fun buildTypes(
            context: BuildContext,
            accumulatedTypes: Set<Stub<Expression>>,
            term: UnionTypeConstructorTerm,
        ): Set<Stub<Expression>> {
            val leftTypeTerm = term.leftType
            val rightTypeTerm = term.rightType

            if (rightTypeTerm is UnionTypeConstructorTerm) {
                throw IllegalArgumentException("Unexpected union type term structure")
            }

            val rightType = Expression.build(
                context = context,
                term = rightTypeTerm,
            )

            val extendedTypes = accumulatedTypes + rightType

            return if (leftTypeTerm is UnionTypeConstructorTerm) {
                buildTypes(
                    context = context,
                    accumulatedTypes = extendedTypes,
                    term = leftTypeTerm,
                )
            } else {
                val leftType = Expression.build(
                    context = context,
                    term = leftTypeTerm,
                )

                extendedTypes + leftType
            }
        }
    }

    override fun bindDirectly(
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

    override val subExpressions: Set<Expression>
        get() = types
}
