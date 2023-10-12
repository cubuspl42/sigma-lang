package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.AnyType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm

class ClassDefinition(
    private val outerScope: StaticScope,
    private val qualifiedPath: QualifiedPath,
    private val term: ClassDefinitionTerm,
) : ConstantDefinition() {
    object Is : StrictBuiltinOrderedFunction() {
        override val argTypes: List<MembershipType> = listOf(
            AnyInstanceType,
            AnyClassType,
        )

        override val imageType: MembershipType = BoolType

        override fun compute(args: List<Value>): Value {
            val instanceValue = args[0] as DictValue
            val classValue = args[1] as DictValue

            val instanceTagValue = instanceValue.readValue(key = instanceTagKey) as Identifier
            val classTagValue = classValue.readValue(key = classTagKey) as Identifier

            return BoolValue(
                value = instanceTagValue == classTagValue,
            )
        }
    }

    companion object {
        val classTagKey = Identifier.of("__classTag__")
        val classTypeKey = Identifier.of("type")

        val instanceTagKey = Identifier.of("__instanceTag__")

        val AnyClassType = UnorderedTupleType(
            valueTypeByName = mapOf(
                classTagKey to AnyType,
                classTypeKey to TypeType,
            ),
        )

        val AnyInstanceType = UnorderedTupleType(
            valueTypeByName = mapOf(
                instanceTagKey to AnyType,
            ),
        )

        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: ClassDefinitionTerm,
        ): ClassDefinition = ClassDefinition(
            outerScope = context.outerScope,
            qualifiedPath = qualifiedPath,
            term = term,
        )
    }

    override val name: Identifier
        get() = term.name

    private val tag = qualifiedPath.toSymbol()

    private val tagType = SymbolType(value = tag)

    private val body by lazy {
        TypeExpression.build(
            outerMetaScope = outerScope,
            term = term.body,
        ).resolved
    }

    private val bodyType: UnorderedTupleType by lazy {
        body.typeOrIllType as UnorderedTupleType
    }

    private val instanceType: UnorderedTupleType by lazy {
        UnorderedTupleType(
            valueTypeByName = bodyType.valueTypeByName + (instanceTagKey to tagType)
        )
    }

    override val valueThunk: Thunk<Value> by lazy {
        Thunk.pure(
            DictValue.fromMap(
                entries = mapOf(
                    classTagKey to tag,
                    classTypeKey to instanceType.asValue,
                    Identifier.of("new") to object : ComputableFunctionValue() {
                        override fun apply(argument: Value): Thunk<Value> {
                            argument as DictValue

                            return Thunk.pure(
                                DictValue.fromMap(
                                    entries = argument.valueEntries + (instanceTagKey to tag)
                                ),
                            )
                        }
                    },
                ),
            ),
        )
    }

    override val computedEffectiveType: Expression.Computation<MembershipType> by lazy {
        Expression.Computation.pure(
            UnorderedTupleType(
                valueTypeByName = mapOf(
                    classTagKey to tagType,
                    classTypeKey to TypeType,
                    Identifier.of("new") to UniversalFunctionType(
                        argumentType = bodyType,
                        imageType = instanceType,
                    ),
                ),
            ),
        )
    }
}
