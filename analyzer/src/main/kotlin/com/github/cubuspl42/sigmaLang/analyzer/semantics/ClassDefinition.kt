package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.QuasiExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.AnyType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm

class ClassDefinition(
    private val outerScope: StaticScope,
    private val qualifiedPath: QualifiedPath,
    private val term: ClassDefinitionTerm,
) : ConstantDefinition(), UserDefinition {
    object Is : StrictBuiltinOrderedFunction() {
        override val argTypes: List<MembershipType> = listOf(
            AnyInstanceType,
            AnyClassType,
        )

        override val imageType: MembershipType = BoolType

        override fun compute(args: List<Value>): Value {
            val instanceValue = args[0] as DictValue
            val classValue = args[1] as DictValue

            val instanceTagValue = instanceValue.read(key = instanceTagKey) as Symbol
            val classTagValue = classValue.read(key = classTagKey) as Symbol

            return BoolValue(
                value = instanceTagValue == classTagValue,
            )
        }
    }

    companion object {
        val classTagKey = Symbol.of("__classTag__")
        val classTypeKey = Symbol.of("type")

        val instanceTagKey = Symbol.of("__instanceTag__")

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
            outerScope: StaticScope,
            qualifiedPath: QualifiedPath,
            term: ClassDefinitionTerm,
        ): ClassDefinition = ClassDefinition(
            outerScope = outerScope,
            qualifiedPath = qualifiedPath,
            term = term,
        )
    }

    override val name: Symbol
        get() = term.name

    private val tag = qualifiedPath.toSymbol()

    private val tagType = SymbolType(value = tag)

    private val classBody by lazy {
        TypeExpression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    private val bodyType: UnorderedTupleType by lazy {
        classBody.typeOrIllType as UnorderedTupleType
    }

    private val instanceType: UnorderedTupleType by lazy {
        UnorderedTupleType(
            valueTypeByName = bodyType.valueTypeByName + (instanceTagKey to tagType)
        )
    }

    override val body: QuasiExpression = object : QuasiExpression() {
        override val computedAnalysis: Expression.Computation<Expression.Analysis?> = Expression.Computation.pure(
            Expression.Analysis(
                inferredType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        classTagKey to tagType,
                        classTypeKey to TypeType,
                        Symbol.of("new") to UniversalFunctionType(
                            argumentType = bodyType,
                            imageType = instanceType,
                        ),
                    ),
                ),
            )
        )

        override val classifiedValue: ClassificationContext<Value> = ConstClassificationContext.pure(
            DictValue(
                entries = mapOf(
                    classTagKey to tag,
                    classTypeKey to instanceType.asValue,
                    Symbol.of("new") to object : ComputableFunctionValue() {
                        override fun apply(argument: Value): Thunk<Value> {
                            argument as DictValue

                            return Thunk.pure(
                                DictValue(
                                    entries = argument.entries + (instanceTagKey to tag)
                                ),
                            )
                        }
                    },
                ),
            ),
        )
    }
}
