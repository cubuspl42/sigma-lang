package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorTerm

abstract class FunctionTypeConstructor : TypeConstructor() {
    abstract override val term: FunctionTypeConstructorTerm

    abstract val metaArgumentType: Expression?

    abstract val argumentType: TupleTypeConstructor

    abstract val imageType: Expression

    companion object {
        fun build(
            context: BuildContext,
            term: FunctionTypeConstructorTerm,
        ): Stub<FunctionTypeConstructor> = object : Stub<FunctionTypeConstructor> {
            override val resolved: FunctionTypeConstructor by lazy {
                object : FunctionTypeConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: FunctionTypeConstructorTerm = term

                    override val metaArgumentType: Expression? by lazy {
                        term.metaArgumentType?.let {
                            TypeExpression.build(
                                outerMetaScope = context.outerMetaScope,
                                term = it,
                            ).resolved
                        }
                    }

                    // TODO: Use the scope of the meta-argument type
                    override val argumentType: TupleTypeConstructor by lazy {
                        TupleTypeConstructor.build(
                            context = context,
                            term = term.argumentType,
                        ).resolved
                    }

                    override val imageType: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.imageType,
                        ).resolved
                    }
                }
            }
        }
    }

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.combine2(
        argumentType.bind(
            dynamicScope = dynamicScope,
        ),
        imageType.bind(
            dynamicScope = dynamicScope,
        ),
    ) { argumentType, imageType ->
        UniversalFunctionType(
            argumentType = argumentType.asType as TupleType,
            imageType = imageType.asType!!,
        ).asValue
    }

    override val subExpressions: Set<Expression>
        get() = setOf(argumentType, imageType)
}
