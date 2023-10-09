package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
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
            context: BuildContext,
            term: UnionTypeConstructorTerm,
        ): UnionTypeConstructor = UnionTypeConstructor(
            outerScope = context.outerScope,
            term = term,
            types = buildTypes(
                context = context,
                accumulatedTypes = emptySet(),
                term = term,
            ),
        )

        private fun buildTypes(
            context: BuildContext,
            accumulatedTypes: Set<Expression>,
            term: UnionTypeConstructorTerm,
        ): Set<Expression> {
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

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.traverseList(types.toList()) { type ->
            type.classifiedValue
        }.transform { typeValues ->
            UnionType(
                memberTypes = typeValues.map { it.asType!! }.toSet(),
            ).asValue
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
