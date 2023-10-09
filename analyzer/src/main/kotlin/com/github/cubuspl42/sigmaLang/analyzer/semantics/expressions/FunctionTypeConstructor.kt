package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorTerm

// I think that this class
class FunctionTypeConstructor(
    override val outerScope: StaticScope,
    override val term: FunctionTypeConstructorTerm,
    val metaArgumentType: TypeExpression?,
    val argumentType: TupleTypeConstructor,
    val imageType: Expression,
) : TypeConstructor() {
    companion object {
        fun build(
            context: BuildContext,
            term: FunctionTypeConstructorTerm,
        ): FunctionTypeConstructor = FunctionTypeConstructor(
            outerScope = context.outerScope,
            term = term,
            metaArgumentType = term.metaArgumentType?.let {
                TypeExpression.build(
                    outerMetaScope = context.outerMetaScope,
                    term = it,
                )
            },
            // TODO: Use the scope of the meta-argument type
            argumentType = TupleTypeConstructor.build(
                context = context,
                term = term.argumentType,
            ),
            imageType = Expression.build(
                context = context,
                term = term.imageType,
            ),
        )
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.transform3(
            metaArgumentType?.body?.classifiedValue ?: ConstClassificationContext.pure(null),
            argumentType.classifiedValue,
            imageType.classifiedValue,
        ) { metaArgumentTypeValue, argumentTypeValue, imageTypeValue ->
            Thunk.pure(
                UniversalFunctionType(
                    metaArgumentType = metaArgumentTypeValue?.asType as TupleType?,
                    argumentType = argumentTypeValue.asType as TupleType,
                    imageType = imageTypeValue.asType as MembershipType,
                ).asValue
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = Thunk.combine3(
        metaArgumentType?.body?.bind(
            dynamicScope = dynamicScope,
        ) ?: Thunk.pure(null),
        argumentType.bind(
            dynamicScope = dynamicScope,
        ),
        imageType.bind(
            dynamicScope = dynamicScope,
        ),
    ) { metaArgumentType, argumentType, imageType ->
        UniversalFunctionType(
            metaArgumentType = metaArgumentType?.asType as TupleType?,
            argumentType = argumentType.asType as TupleType,
            imageType = imageType.asType as MembershipType,
        ).asValue
    }

    override val subExpressions: Set<Expression> = setOf(argumentType, imageType)
}
